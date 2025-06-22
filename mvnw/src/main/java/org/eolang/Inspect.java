/*
 * SPDX-FileCopyrightText: Copyright (c) 2022-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang;

import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtBasic;
import org.takes.rq.RqPrint;
import org.takes.rs.RsText;

/**
 * HTTP inspection server.
 * @since 0.29.0
 */
public final class Inspect {
    /**
     * Prevents instantiation (required by qulice UseUtilityClass rule).
     */
    private Inspect() {
        // Intentionally empty
    }

    /**
     * Main entry point.
     * @param args Command line arguments
     * @throws IOException If server fails to start
     */
    public static void main(final String... args) throws IOException {
        Current.get();
        new FtBasic(
            new TkFork(
                new FkRegex("/ls", new TkLs()),
                new FkRegex("/go/(?<attr>[a-zA-Z0-9_.@]+)", new TkGo()),
                new FkRegex("^/up$", new TkUp()),
                new FkRegex("/add/(?<attr>[a-zA-Z0-9_@]+)", new TkAdd()),
                new FkRegex("/rm/(?<attr>[a-zA-Z0-9_.@]+)", new TkRm()),
                new FkRegex("/cp/(?<attr>[a-zA-Z0-9_]+)", new TkCp()),
                new FkRegex("/to/(?<attr>[a-zA-Z0-9_.]+)", new TkTo()),
                new FkRegex("/dd/(?<attr>[a-zA-Z0-9_]+)/(?<path>[a-zA-Z0-9_\\.]+)", new TkDd()),
                new FkRegex("/form", new TkForm()),
                new FkRegex("/put/(?<bytes>[0-9A-Fa-f\\-]+)", new TkPut()),
                new FkRegex("/run", new TkRun()),
                new FkRegex(
                    "/echo",
                    (Take) req -> new RsText(new RqPrint(req).printBody())
                ),
                new FkRegex(
                    "^/$",
                    (Take) req -> new RsText(Current.formatPathWithChildren())
                )
            ),
            8080
        ).start(() -> Thread.currentThread().isInterrupted());
    }
}
