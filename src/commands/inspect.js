/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
const { spawn } = require('child_process');
const path = require('path');
const readline = require('readline');
const fetch = require('node-fetch');

module.exports = function(opts) {
  return new Promise((resolve, reject) => {
    const mvnDir = path.join(process.cwd(), 'mvnw');
    const eocDir = path.join(process.cwd(), '.eoc');
    const jar = path.join(mvnDir, 'target', 'inspect.jar');
    const eoc = path.join(eocDir,'eoc.jar');
    const separator = process.platform === 'win32' ? ';' : ':';

    const server = spawn('java', [
      '-cp',
      jar,
      'org.eolang.Inspect'
    ], { cwd: mvnDir, stdio: ['pipe', 'pipe', 'pipe'] });

    server.stdout.setEncoding('utf8').on('data', d => console.log(`[SERVER] ${d}`));
    server.stderr.setEncoding('utf8').on('data', d => console.error(`[SERVER ERROR] ${d}`));

    setTimeout(() => {
      (async () => {
        console.info('EO inspect server started.');

        try {
          const greeting = await fetch('http://localhost:8080/');
          const text = await greeting.text();
          console.log(text); // Выводим приветствие один раз перед интерактивом
        } catch (err) {
          console.error('Failed to get greeting:', err.message);
        }

        const rl = readline.createInterface({
          input: (opts && opts.stdin) || process.stdin,
          output: (opts && opts.stdout) || process.stdout,
          terminal: true
        });

        const ask = (q) => new Promise(res => rl.question(q, res));

        const processInput = async () => {
          const input = await ask('Enter command (or type "exit" to quit): ');
          const trimmed = input.trim();
          if (trimmed === 'help') {
            console.log(`
          Available commands:

            ls                  List attributes of the current object
            go <attr>           Enter attribute
            .<attr>             Same as 'go <attr>'
            ..                  Go up one level
            run / dataize       Dataize current object
            add <name> / +<n>   Add empty attribute with name
            rm <name> / -<n>    Remove attribute with name
            cp Φ.<name>         Copy current object to φ.<name>
            to Φ.<path>         Attach object from φ.<path> to current object
            dd <a> Φ.<path>     Dispatch attribute <a> and attach result to φ.<path>
            form                Create an empty formation and attach to the current object
            put <bytes>         Set raw bytes as Δ (format: 48-65-6C-6C-6F)
            help                Show this message
            exit                Quit inspector
            `);
          } else if (trimmed.toLowerCase() === 'exit') {
            rl.close();
            server.kill();
            return resolve();
          } else if (trimmed.startsWith("rm ") || trimmed.startsWith("-")) {
            let attr = trimmed.startsWith("rm ") ? trimmed.slice(3).trim() : trimmed.slice(1).trim();
            const response = await fetch(`http://localhost:8080/rm/${attr}`);
            const text = await response.text();
            console.log(text);
          } else if (trimmed.startsWith('add ') || trimmed.startsWith('+')) {
            let name = trimmed.startsWith('add ') ? trimmed.slice(4).trim() : trimmed.slice(1).trim();
            if (!name) {
              console.log('Specify attribute name to add.');
            } else {
              const response = await fetch(`http://localhost:8080/add/${name}`);
              const text = await response.text();
              console.log(text);
            }
          } else if (trimmed === '..') {
            try {
              const response = await fetch('http://localhost:8080/up');
              const text = await response.text();
              console.log(text);
            } catch (err) {
              console.error('Request failed:', err.message);
            }
          } else if (trimmed === 'ls') {
            try {
              const response = await fetch('http://localhost:8080/ls');
              const text = await response.text();
              console.log(text);
            } catch (err) {
              console.error('Request failed:', err.message);
            }
          } else if (trimmed.startsWith('go ') || trimmed.startsWith('.')) {
            let attr = trimmed.startsWith('go ') ? trimmed.slice(3).trim() : trimmed.slice(1).trim();
            if (!attr) {
              console.log('Specify attribute name after "go" or "."');
            } else {
              try {
                const response = await fetch(`http://localhost:8080/go/${attr}`);
                const text = await response.text();
                console.log(text);
              } catch (err) {
                console.error('Request failed:', err.message);
              }
            }
          } else if (trimmed.startsWith('cp Φ.')) {
              const attr = trimmed.slice('cp Φ.'.length).trim();
              if (!attr.match(/^[a-zA-Z0-9_]+$/)) {
                console.log('Invalid attribute name.');
              } else {
                try {
                  const response = await fetch(`http://localhost:8080/cp/${attr}`);
                  const text = await response.text();
                  console.log(text);
                } catch (err) {
                  console.error('Request failed:', err.message);
                }
              }
          } else if (trimmed.startsWith('to Φ.')) {
              const attr = trimmed.slice('to Φ.'.length).trim();
              if (!attr.match(/^[a-zA-Z0-9_.]+$/)) {
                console.log('Invalid attribute name.');
              } else {
                try {
                  const response = await fetch(`http://localhost:8080/to/${attr}`);
                  const text = await response.text();
                  console.log(text);
                } catch (err) {
                  console.error('Request failed:', err.message);
                }
              }
            } else if (trimmed.startsWith('dd ')) {
                const parts = trimmed.split(' ');
                if (parts.length !== 3 || !parts[1].match(/^[a-zA-Z0-9_]+$/) || !parts[2].startsWith('Φ.')) {
                    console.log('Usage: dd foo Φ.bar');
                } else {
                    const attr = parts[1];
                    const path = parts[2].slice('Φ.'.length);
                    try {
                        const response = await fetch(`http://localhost:8080/dd/${attr}/${path}`);
                        const text = await response.text();
                        console.log(text);
                    } catch (err) {
                        console.error('Request failed:', err.message);
                    }
                }
            } else if (trimmed === 'form') {
                try {
                    const response = await fetch('http://localhost:8080/form');
                    const text = await response.text();
                    console.log(text);
                } catch (err) {
                    console.error('Request failed:', err.message);
                }
            } else if (trimmed.startsWith('put ')) {
                let bytes = trimmed.slice(4).trim();
                if (!bytes.match(/^([0-9A-Fa-f]{2}(-[0-9A-Fa-f]{2})*)$/)) {
                    console.log('Invalid bytes format. Expected like: 04-05-96-92-2F-E3');
                } else {
                    try {
                        const response = await fetch(`http://localhost:8080/put/${bytes}`);
                        const text = await response.text();
                        console.log(text);
                    } catch (err) {
                        console.error('Request failed:', err.message);
                    }
                }
            } else if (trimmed === 'run' || trimmed === 'dataize') {
                try {
                  const response = await fetch('http://localhost:8080/run');
                  const text = await response.text();
                  console.log(text);
                  } catch (err) {
                    console.error('Request failed:', err.message);
                  }
            } else {
                console.log('Unknown command. Use "ls", "go <attr>", ".<attr>", "..", or "exit".');
            }
          processInput();
        };
        processInput();
      })();
    }, 2000);
    server.on('close', code => console.info(`Server stopped with code ${code}`));
  });
};
