package com.github.nstdio.libpgn.core.filter;

import com.github.nstdio.libpgn.core.TagPair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

class TagPairFilter implements Predicate<List<TagPair>> {
    final String name;
    final String value;

    TagPairFilter(final String name, final String value) {
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
    }

    private static TagPair find(final List<TagPair> input, final String name) {
        return input
                .stream()
                .filter(tagPair -> tagPair.getTag().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    TagPair named(final List<TagPair> input) {
        return find(input, name);
    }

    @Override
    public boolean test(final List<TagPair> input) {
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

        static Predicate<List<TagPair>> whiteLastNameEquals(final String lastName) {
            return new LastNameEquals("White", lastName);
        }

        static Predicate<List<TagPair>> blackLastNameEquals(final String lastName) {
            return new LastNameEquals("Black", lastName);
        }

        @Override
        public boolean test(final List<TagPair> input) {
            final TagPair tagPair = named(input);
            if (tagPair != null) {
                final String tagPairValue = tagPair.getValue();
                final int commaIdx = tagPairValue.indexOf(',');

                if (value.equals(commaIdx == -1 ? tagPairValue : tagPairValue.substring(0, commaIdx))) {
                    return true;
                }
            }

            return false;
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

        static Predicate<List<TagPair>> whiteGreaterThen(final int elo) {
            return new Elo("WhiteElo", elo) {
                @Override
                boolean test(int elo) {
                    return elo > this.elo;
                }
            };
        }

        static Predicate<List<TagPair>> whiteLessThen(final int elo) {
            return new Elo("WhiteElo", elo) {
                @Override
                boolean test(int elo) {
                    return elo < this.elo;
                }
            };
        }

        static Predicate<List<TagPair>> blackGreaterThen(final int elo) {
            return new Elo("BlackElo", elo) {
                @Override
                boolean test(int elo) {
                    return elo > this.elo;
                }
            };
        }

        static Predicate<List<TagPair>> blackLessThen(final int elo) {
            return new Elo("BlackElo", elo) {
                @Override
                boolean test(int elo) {
                    return elo < this.elo;
                }
            };
        }

        @Override
        public boolean test(final List<TagPair> input) {
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
    abstract static class YearFilter implements Predicate<List<TagPair>> {
        final int year;

        private YearFilter(int year) {
            this.year = year;
        }

        static Predicate<List<TagPair>> yearEquals(final int year) {
            return new YearFilter(year) {
                @Override
                boolean test(int year) {
                    return year == this.year;
                }
            };
        }

        static Predicate<List<TagPair>> yearGreaterThen(final int year) {
            return new YearFilter(year) {
                @Override
                boolean test(int year) {
                    return year > this.year;
                }
            };
        }

        static Predicate<List<TagPair>> yearLessThen(final int year) {
            return new YearFilter(year) {
                @Override
                boolean test(int year) {
                    return year < this.year;
                }
            };
        }

        @Override
        public final boolean test(final List<TagPair> input) {
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
