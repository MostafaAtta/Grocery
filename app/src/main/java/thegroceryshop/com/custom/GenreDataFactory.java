package thegroceryshop.com.custom;


import java.util.Arrays;
import java.util.List;

import thegroceryshop.com.R;


public class GenreDataFactory {

    public static List<Genre> makeGenres() {
        return Arrays.asList(
                makeList(),
                makeMyAccount(),
                makeReserve(),
                makeExplore(),
                makeSale(),
                makeNew(),
                makeMarketplace(),
                makeFresh(),
                makeSeaFood(),
                makeDairy(),
                makeBakery(),
                makeFrozen(),
                makeBeverages(),
                makeHealth(),
                makeBabyProducts(),
                makeFeedback()
        );
    }


    public static List<Artist> makeChild1() {


      //  Artist q = new Artist("List", true);
        Artist q = new Artist("", true);
        return Arrays.asList(q);
    }

    public static List<Artist> makeChild2() {


    //    Artist q = new Artist("My Account", true);
        Artist q = new Artist("", true);
        return Arrays.asList(q);
    }

    public static List<Artist> makeChild3() {


        //Artist q = new Artist("Reserve a Delivery Slot", true);
        Artist q = new Artist("", true);
        return Arrays.asList(q);
    }

    public static List<Artist> makeChild4() {


       // Artist q = new Artist("Explore", true);
        Artist q = new Artist("", true);

        return Arrays.asList(q);
    }

    public static List<Artist> makeChild5() {


      //  Artist q = new Artist("On Sale", true);
        Artist q = new Artist("", true);
        return Arrays.asList(q);
    }

    public static List<Artist> makeChild6() {


        //Artist q = new Artist("New", true);
        Artist q = new Artist("", true);
        return Arrays.asList(q);
    }

    public static List<Artist> makeChild7() {


        //Artist q = new Artist("Marketplace", true);
        Artist q = new Artist("", true);
        return Arrays.asList(q);
    }

    public static List<Artist> makeChild8() {


        Artist q = new Artist("Fresh Products", true);

        return Arrays.asList(q);
    }

    public static List<Artist> makeChild9() {


        Artist q = new Artist("Meat & Seafood", true);

        return Arrays.asList(q);
    }

    public static List<Artist> makeChild10() {


        Artist q = new Artist("Dairy & Chilled", true);

        return Arrays.asList(q);
    }

    public static List<Artist> makeChild11() {


        Artist q = new Artist("Bakery", true);

        return Arrays.asList(q);
    }

    public static List<Artist> makeChild12() {


        Artist q = new Artist("Frozen", true);

        return Arrays.asList(q);
    }

    public static List<Artist> makeChild13() {


        Artist q = new Artist("Beverages", true);

        return Arrays.asList(q);
    }

    public static List<Artist> makeChild14() {


        Artist q = new Artist("Health & Beauty", true);

        return Arrays.asList(q);
    }

    public static List<Artist> makeChild15() {


        Artist q = new Artist("Baby & Child", true);

        return Arrays.asList(q);
    }

    public static List<Artist> makeChild16() {


        Artist q = new Artist("Feedback", true);

        return Arrays.asList(q);
    }


    public static List<Artist> makeJazzArtists() {
        Artist milesDavis = new Artist("Miles Davis", true);
        Artist ellaFitzgerald = new Artist("Ella Fitzgerald", true);
        Artist billieHoliday = new Artist("Billie Holiday", false);

        return Arrays.asList(milesDavis, ellaFitzgerald, billieHoliday);
    }


    public static List<Artist> makeClassicArtists() {
        Artist beethoven = new Artist("Ludwig van Beethoven", false);
        Artist bach = new Artist("Johann Sebastian Bach", true);
        Artist brahms = new Artist("Johannes Brahms", false);
        Artist puccini = new Artist("Giacomo Puccini", false);

        return Arrays.asList(beethoven, bach, brahms, puccini);
    }


    public static List<Artist> makeSalsaArtists() {
        Artist hectorLavoe = new Artist("Hector Lavoe", true);
        Artist celiaCruz = new Artist("Celia Cruz", false);
        Artist willieColon = new Artist("Willie Colon", false);
        Artist marcAnthony = new Artist("Marc Anthony", false);

        return Arrays.asList(hectorLavoe, celiaCruz, willieColon, marcAnthony);
    }


    public static Genre makeList() {


        return new Genre("List", null, R.mipmap.list_icon_1);
    }

    public static Genre makeMyAccount() {


        return new Genre("My Account",null, R.mipmap.list_icon_2);
    }

    public static Genre makeReserve() {

        return new Genre("Reserve a Delivery Slot", null, R.mipmap.list_icon_3);
    }

    public static Genre makeExplore() {


        return new Genre("Explore", null, R.mipmap.list_icon_4);
    }

    public static Genre makeSale() {


        return new Genre("On Sale", null, R.mipmap.list_icon_5);
    }

    public static Genre makeNew() {


        return new Genre("New", null, R.mipmap.list_icon_6);
    }

    public static Genre makeMarketplace() {


        return new Genre("Marketplace", null, R.mipmap.list_icon_7);
    }

    public static Genre makeFresh() {


        return new Genre("Fresh Products", null, R.mipmap.list_icon_8);
    }

    public static Genre makeSeaFood() {


        return new Genre("Meat & Seafood", null, R.mipmap.list_icon_9);
    }

    public static Genre makeDairy() {


        return new Genre("Dairy & Chilled", null, R.mipmap.list_icon_10);
    }

    public static Genre makeBakery() {


        return new Genre("Bakery", null, R.mipmap.list_icon_11);
    }

    public static Genre makeFrozen() {


        return new Genre("Frozen",null, R.mipmap.list_icon_12);
    }

    public static Genre makeBeverages() {


        return new Genre("Beverages", null, R.mipmap.list_icon_13);
    }

    public static Genre makeHealth() {


        return new Genre("Health & Beauty",null, R.mipmap.list_icon_14);
    }

    public static Genre makeBabyProducts() {


        return new Genre("Baby & Child", null, R.mipmap.list_icon_15);
    }

    public static Genre makeFeedback() {


        return new Genre("Feedback", null, R.mipmap.list_icon_16);
    }
}

























