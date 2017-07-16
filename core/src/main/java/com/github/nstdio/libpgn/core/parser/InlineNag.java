package com.github.nstdio.libpgn.core.parser;

import com.github.nstdio.libpgn.core.NAG;
import com.github.nstdio.libpgn.core.internal.CollectionUtils;
import com.github.nstdio.libpgn.core.internal.Pair;

import java.util.Set;
import java.util.TreeSet;

class InlineNag {

    Pair<String, short[]> split(String move) {
        Set<Short> nags = new TreeSet<>();

        int firstOccurrence = -1;

        for (int i = 2, n = move.length(); i < n; i++) {
            final char ch = move.charAt(i);

            final short nag = NAG.indexOf(ch);

            if (nag != -1 && firstOccurrence == -1) {
                firstOccurrence = i;
            }

            if (i + 1 < n) {
                final char nCh = move.charAt(i + 1);
                switch (ch) {
                    case '!':
                        switch (nCh) {
                            case '?':
                                i++;
                                nags.add((short) 5);
                                break;
                            case '!':
                                i++;
                                nags.add((short) 3);
                                break;
                            default:
                                nags.add(nag);
                                break;
                        }
                        break;
                    case '?':
                        switch (nCh) {
                            case '!':
                                i++;
                                nags.add((short) 6);
                                break;
                            case '?':
                                i++;
                                nags.add((short) 4);
                                break;
                            default:
                                nags.add(nag);
                                break;
                        }
                        break;
                    case '+':
                        switch (nCh) {
                            case '-':
                                if (firstOccurrence == -1) {
                                    firstOccurrence = i;
                                }
                                i++;
                                nags.add((short) 18);
                                break;
                        }
                        break;
                    case '-':
                        switch (nCh) {
                            case '+':
                                if (firstOccurrence == -1) {
                                    firstOccurrence = i;
                                }
                                i++;
                                nags.add((short) 19);
                                break;
                        }
                    case 'R':
                        switch (nCh) {
                            case 'R':
                                if (firstOccurrence == -1) {
                                    firstOccurrence = i;
                                }
                                i++;
                                nags.add((short) 145);
                                break;
                        }
                        break;
                    case 'N':
                        nags.add(nag);
                        break;
                    default:
                        if (nag != -1) {
                            nags.add(nag);
                        }
                        break;
                }
            } else if (nag != -1) {
                nags.add(nag);
            }
        }

        if (firstOccurrence == -1) {
            return Pair.of(move, null);
        }

        return Pair.of(
                move.substring(0, firstOccurrence),
                CollectionUtils.toArray(nags)
        );
    }
}
