package com.github.nstdio.libpgn.core;

import java.util.HashMap;
import java.util.Map;

public class NAG {
    private static final Map<Integer, String> pos = new HashMap<>(35);
    private static final Map<String, Integer> rev = new HashMap<>(35);

    private static final String[] interp = {
            "\0",
            "Good move",
            "Poor move",
            "Very good move",
            "Very poor move",
            "Speculative move",
            "Questionable move",
            "Forced move (all others lose quickly)",
            "Singular move (no reasonable alternatives)",
            "Worst move",
            "Drawish position",
            "Equal chances, quiet position",
            "Equal chances, active position",
            "Unclear position",
            "White has a slight advantage",
            "Black has a slight advantage",
            "White has a moderate advantage",
            "Black has a moderate advantage",
            "White has a decisive advantage",
            "Black has a decisive advantage",
            "White has a crushing advantage (Black should resign)",
            "Black has a crushing advantage (White should resign)",
            "White is in zugzwang",
            "Black is in zugzwang",
            "White has a slight space advantage",
            "Black has a slight space advantage",
            "White has a moderate space advantage",
            "Black has a moderate space advantage",
            "White has a decisive space advantage",
            "Black has a decisive space advantage",
            "White has a slight time (development) advantage",
            "Black has a slight time (development) advantage",
            "White has a moderate time (development) advantage",
            "Black has a moderate time (development) advantage",
            "White has a decisive time (development) advantage",
            "Black has a decisive time (development) advantage",
            "White has the initiative",
            "Black has the initiative",
            "White has a lasting initiative",
            "Black has a lasting initiative",
            "White has the attack",
            "Black has the attack",
            "White has insufficient compensation for material deficit",
            "Black has insufficient compensation for material deficit",
            "White has sufficient compensation for material deficit",
            "Black has sufficient compensation for material deficit",
            "White has more than adequate compensation for material deficit",
            "Black has more than adequate compensation for material deficit",
            "White has a slight center control advantage",
            "Black has a slight center control advantage",
            "White has a moderate center control advantage",
            "Black has a moderate center control advantage",
            "White has a decisive center control advantage",
            "Black has a decisive center control advantage",
            "White has a slight kingside control advantage",
            "Black has a slight kingside control advantage",
            "White has a moderate kingside control advantage",
            "Black has a moderate kingside control advantage",
            "White has a decisive kingside control advantage",
            "Black has a decisive kingside control advantage",
            "White has a slight queenside control advantage",
            "Black has a slight queenside control advantage",
            "White has a moderate queenside control advantage",
            "Black has a moderate queenside control advantage",
            "White has a decisive queenside control advantage",
            "Black has a decisive queenside control advantage",
            "White has a vulnerable first rank",
            "Black has a vulnerable first rank",
            "White has a well protected first rank",
            "Black has a well protected first rank",
            "White has a poorly protected king",
            "Black has a poorly protected king",
            "White has a well protected king",
            "Black has a well protected king",
            "White has a poorly placed king",
            "Black has a poorly placed king",
            "White has a well placed king",
            "Black has a well placed king",
            "White has a very weak pawn structure",
            "Black has a very weak pawn structure",
            "White has a moderately weak pawn structure",
            "Black has a moderately weak pawn structure",
            "White has a moderately strong pawn structure",
            "Black has a moderately strong pawn structure",
            "White has a very strong pawn structure",
            "Black has a very strong pawn structure",
            "White has poor knight placement",
            "Black has poor knight placement",
            "White has good knight placement",
            "Black has good knight placement",
            "White has poor bishop placement",
            "Black has poor bishop placement",
            "White has good bishop placement",
            "Black has good bishop placement",
            "White has poor rook placement",
            "Black has poor rook placement",
            "White has good rook placement",
            "Black has good rook placement",
            "White has poor queen placement",
            "Black has poor queen placement",
            "White has good queen placement",
            "Black has good queen placement",
            "White has poor piece coordination",
            "Black has poor piece coordination",
            "White has good piece coordination",
            "Black has good piece coordination",
            "White has played the opening very poorly",
            "Black has played the opening very poorly",
            "White has played the opening poorly",
            "Black has played the opening poorly",
            "White has played the opening well",
            "Black has played the opening well",
            "White has played the opening very well",
            "Black has played the opening very well",
            "White has played the middlegame very poorly",
            "Black has played the middlegame very poorly",
            "White has played the middlegame poorly",
            "Black has played the middlegame poorly",
            "White has played the middlegame well",
            "Black has played the middlegame well",
            "White has played the middlegame very well",
            "Black has played the middlegame very well",
            "White has played the ending very poorly",
            "Black has played the ending very poorly",
            "White has played the ending poorly",
            "Black has played the ending poorly",
            "White has played the ending well",
            "Black has played the ending well",
            "White has played the ending very well",
            "Black has played the ending very well",
            "White has slight counterplay",
            "Black has slight counterplay",
            "White has moderate counterplay",
            "Black has moderate counterplay",
            "White has decisive counterplay",
            "Black has decisive counterplay",
            "White has moderate time control pressure",
            "Black has moderate time control pressure",
            "White has severe time control pressure",
            "Black has severe time control pressure",
    };

    static {
        pos.put(1, "\u0021");
        pos.put(2, "\u003F");
        pos.put(3, "\u203C");
        pos.put(4, "\u2047");
        pos.put(5, "\u2049");
        pos.put(6, "\u2048");
        pos.put(7, "\u25A1");
        pos.put(10, "\u003D");
        pos.put(13, "\u221E");
        pos.put(14, "\u2A72");
        pos.put(15, "\u2A71");
        pos.put(16, "\u0177");
        pos.put(17, "\u2213");
        pos.put(18, "+-");
        pos.put(19, "-+");
        pos.put(22, "\u2A00");
        pos.put(23, "\u2A00");
        pos.put(32, "\u27F3");
        pos.put(36, "\u2192");
        pos.put(37, "\u2192");
        pos.put(40, "\u2191");
        pos.put(41, "\u2191");
        pos.put(132, "\u21C6");
        pos.put(133, "\u21C6");
        pos.put(140, "\u2206");
        pos.put(142, "\u2313");
        pos.put(145, "RR");
        pos.put(146, "N");
        pos.put(239, "\u21D4");
        pos.put(240, "\u21D7");
        pos.put(242, "\u27EB");
        pos.put(243, "\u27EA");
        pos.put(244, "\u2715");
        pos.put(245, "\u22A5");

        for (Map.Entry<Integer, String> entry : pos.entrySet()) {
            rev.put(entry.getValue(), entry.getKey());
        }
    }

    static String glyphAtIndex(final int index) {
        final String glyph = pos.get(index);

        return glyph == null ? "" : glyph;
    }

    public static short indexOf(final String nag) {
        final Integer nagInteger = rev.get(nag);

        return nagInteger == null ? -1 : nagInteger.shortValue();
    }

    public static short indexOf(final char nag) {
        return indexOf(String.valueOf(nag));
    }

    public static String descriptionOf(final short[] indices, String delim) {
        if (indices == null) {
            return "";
        }

        switch (indices.length) {
            case 1:
                return interp[indices[0]];
            case 2:
                return interp[indices[0]] + delim + interp[indices[1]];
            case 3:
                return interp[indices[0]] + delim + interp[indices[1]] + delim + interp[indices[2]];
            case 4:
                return interp[indices[0]] + delim + interp[indices[1]] + delim + interp[indices[2]] + delim + interp[indices[3]];
        }

        StringBuilder sb = new StringBuilder();

        for (short idx : indices) {
            sb.append(interp[idx])
                    .append(delim);
        }

        final String desc = sb.toString();

        return desc.substring(0, desc.length() - delim.length());
    }

    static String glyphAtIndices(final short[] indices) {
        StringBuilder sb = new StringBuilder(16);

        for (short idx : indices) {
            sb.append(glyphAtIndex(idx));
        }

        return sb.toString();
    }
}
