/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

const fs = require('fs');
const path = require('path');
const {runSync, assertFilesExist, parserVersion, homeHash} = require('../helpers');

describe('test', function() {
  it('executes a single unit test', function(done) {
    home = path.resolve('temp/test-test/simple');
    fs.rmSync(home, {recursive: true, force: true});
    fs.mkdirSync(path.resolve(home, 'src'), {recursive: true});
    fs.writeFileSync(
      path.resolve(home, 'src/simple-test.eo'),
      [
        '+junit',
        '',
        '# sample',
        '[] > simple-comparison-works',
        '  gt. > @',
        '    10',
        '    5',
      ].join('\n')
    );
    const stdout = runSync([
      'test',
      '--verbose',
      '--parser=' + parserVersion,
      '--hash=' + homeHash,
      '-s', path.resolve(home, 'src'),
      '-t', path.resolve(home, 'target'),
    ]);
    assertFilesExist(
      stdout, home,
      [
        'target/generated-sources/EOsimple_comparison_worksTest.java',
        'target/classes/EOsimple_comparison_worksTest.class',
      ]
    );
    done();
  });
});
