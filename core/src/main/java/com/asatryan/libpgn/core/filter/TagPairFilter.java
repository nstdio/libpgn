package com.asatryan.libpgn.core.filter;

import com.asatryan.libpgn.core.TagPair;

import javax.annotation.Nullable;
import java.util.List;

import static com.asatryan.libpgn.core.filter.TagPairFilter.Elo.*;

class TagPairFilter implements Filter<List<TagPair>> {
    final String name;
    final String value;

    TagPairFilter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    private static TagPair find(final List<TagPair> input, final String name) {
        for (TagPair tagPair : input) {
            if (tagPair.getTag().equals(name)) {
                return tagPair;
            }
        }

        return null;
    }

    @Nullable
    TagPair named(final List<TagPair> input) {
        return find(input, name);
    }

    @Override
    public boolean test(List<TagPair> input) {
        final TagPair named = named(input);

        return named != null && named.getValue().equals(value);

    }

    /**
     *
     */
    static class LastNameEquals extends TagPairFilter {
        private LastNameEquals(final String tag, final String lastName) {
            super(tag, lastName);
        }

        static Filter<List<TagPair>> whiteLastNameEquals(final String lastName) {
            return new LastNameEquals("White", lastName);
        }

        static Filter<List<TagPair>> blackLastNameEquals(final String lastName) {
            return new LastNameEquals("Black", lastName);
        }

        static Filter<List<TagPair>> lastNameEquals(final String lastName) {
            return new PlayerLastNameEquals(lastName);
        }

        @Override
        public boolean test(List<TagPair> input) {
            final TagPair tagPair = named(input);
            if (tagPair != null) {
                String[] parts = tagPair.getValue().split(",");
                if (parts[0].equals(value)) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     *
     */
    private static class PlayerLastNameEquals implements Filter<List<TagPair>> {
        private final Filter<List<TagPair>> whiteFilter;
        private final Filter<List<TagPair>> blackFilter;

        PlayerLastNameEquals(final String lastName) {
            whiteFilter = LastNameEquals.whiteLastNameEquals(lastName);
            blackFilter = LastNameEquals.blackLastNameEquals(lastName);
        }

        @Override
        public boolean test(List<TagPair> input) {
            return whiteFilter.test(input) || blackFilter.test(input);
        }
    }

    /**
     *
     */
    abstract static class Elo extends TagPairFilter {
        final int elo;

        private Elo(final String name, final int value) {
            super(name, String.valueOf(value));
            elo = value;
        }

        static Filter<List<TagPair>> whiteGreaterThen(final int elo) {
            return new Elo("WhiteElo", elo) {
                @Override
                boolean test(int elo) {
                    return elo > this.elo;
                }
            };
        }

        static Filter<List<TagPair>> whiteLessThen(final int elo) {
            return new Elo("WhiteElo", elo) {
                @Override
                boolean test(int elo) {
                    return elo < this.elo;
                }
            };
        }

        static Filter<List<TagPair>> blackGreaterThen(final int elo) {
            return new Elo("BlackElo", elo) {
                @Override
                boolean test(int elo) {
                    return elo > this.elo;
                }
            };
        }

        static Filter<List<TagPair>> blackLessThen(final int elo) {
            return new Elo("BlackElo", elo) {
                @Override
                boolean test(int elo) {
                    return elo < this.elo;
                }
            };
        }

        @Override
        public boolean test(List<TagPair> input) {
            final TagPair tagPair = named(input);

            if (tagPair != null) {
                try {
                    final int playerElo = Integer.parseInt(tagPair.getValue());
                    return test(playerElo);
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            return false;
        }

        abstract boolean test(final int elo);
    }

    /**
     *
     */
    abstract static class PlayerElo implements Filter<List<TagPair>> {
        private final Filter<List<TagPair>> white;
        private final Filter<List<TagPair>> black;

        PlayerElo(Filter<List<TagPair>> white, Filter<List<TagPair>> black) {
            this.white = white;
            this.black = black;
        }

        static PlayerElo greaterThen(final int elo) {
            return new PlayerElo(whiteGreaterThen(elo), whiteGreaterThen(elo)) {
                @Override
                public boolean test(List<TagPair> input) {
                    return super.test(input);
                }
            };
        }

        static PlayerElo lessThen(final int elo) {
            return new PlayerElo(whiteLessThen(elo), blackLessThen(elo)) {
                @Override
                public boolean test(List<TagPair> input) {
                    return super.test(input);
                }
            };
        }

        @Override
        public boolean test(List<TagPair> input) {
            return white.test(input) || black.test(input);
        }
    }

    /**
     *
     */
    abstract static class YearFilter implements Filter<List<TagPair>> {
        final int year;

        private YearFilter(int year) {
            this.year = year;
        }

        static Filter<List<TagPair>> yearEquals(final int year) {
            return new YearFilter(year) {
                @Override
                boolean test(int year) {
                    return year == this.year;
                }
            };
        }

        static Filter<List<TagPair>> yearGreaterThen(final int year) {
            return new YearFilter(year) {
                @Override
                boolean test(int year) {
                    return year > this.year;
                }
            };
        }

        static Filter<List<TagPair>> yearGreaterThenOrEquals(final int year) {
            return new YearFilter(year) {
                @Override
                boolean test(int year) {
                    return year >= this.year;
                }
            };
        }

        static Filter<List<TagPair>> yearLessThen(final int year) {
            return new YearFilter(year) {
                @Override
                boolean test(int year) {
                    return year < this.year;
                }
            };
        }

        static Filter<List<TagPair>> yearLessThenOrEquals(final int year) {
            return new YearFilter(year) {
                @Override
                boolean test(int year) {
                    return year <= this.year;
                }
            };
        }

        static Filter<List<TagPair>> yearBetween(final int start, final int end) {
            if (end < start) {
                throw new IllegalArgumentException("end < start");
            }

            if (start == end) {
                return yearEquals(start);
            }

            return new YearFilter(start) {
                @Override
                boolean test(final int year) {
                    return year >= this.year && year <= end;
                }
            };
        }

        @Override
        public final boolean test(List<TagPair> input) {
            final TagPair tagPair = TagPairFilter.find(input, "Date");
            if (tagPair != null) {
                final String value = tagPair.getValue();

                try {
                    return test(Integer.parseInt(value.substring(0, 4)));
                } catch (StringIndexOutOfBoundsException | NumberFormatException ignored) {
                    // fall through
                }
            }

            return false;
        }

        abstract boolean test(final int year);
    }
}
