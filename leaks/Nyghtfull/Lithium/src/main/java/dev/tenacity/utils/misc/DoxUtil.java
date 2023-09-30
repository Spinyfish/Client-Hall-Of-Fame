package dev.tenacity.utils.misc;

import dev.tenacity.utils.Utils;

/**
 * @author cedo
 * @since 03/15/2022
 */
public class DoxUtil implements Utils {
    private static final char[] alphaNumeric = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private static final char[] numeric = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private static final String[] states = {
            "Alabama",
            "Alaska",
            "Arizona",
            "Arkansas",
            "California",
            "Colorado",
            "Connecticut",
            "Delaware",
            "Florida",
            "Georgia",
            "Hawaii",
            "Idaho",
            "Illinois",
            "Indiana",
            "Iowa",
            "Kansas",
            "Kentucky",
            "Louisiana",
            "Maine",
            "Maryland",
            "Massachusetts",
            "Michigan",
            "Minnesota",
            "Mississippi",
            "Missouri",
            "Montana",
            "Nebraska",
            "Nevada",
            "New Hampshire",
            "New Jersey",
            "New Mexico",
            "New York",
            "North Carolina",
            "North Dakota",
            "Ohio",
            "Oklahoma",
            "Oregon",
            "Pennsylvania",
            "Rhode Island",
            "South Carolina",
            "South Dakota",
            "Tennessee",
            "Texas",
            "Utah",
            "Vermont",
            "Virginia",
            "Washington",
            "West Virginia",
            "Wisconsin",
            "Wyoming"
    };

    private static final String[] streetNames = {
            "Sunset Drive",
            "Vine Street",
            "Woodland Drive",
            "6th Street West",
            "Brookside Drive",
            "Hillside Avenue",
            "Lake Street",
            "13th Street",
            "4th Avenue",
            "5th Street North",
            "College Street",
            "Dogwood Lane",
            "Mill Road",
            "7th Avenue",
            "8th Street",
            "Beech Street",
            "Division Street",
            "Harrison Street",
            "Heather Lane",
            "Lakeview Drive",
            "Laurel Lane",
            "New Street",
            "Oak Lane",
            "Primrose Lane",
            "Railroad Street",
            "Willow Street",
            "4th Street North",
            "5th Street West",
            "6th Avenue",
            "Berkshire Drive",
            "Buckingham Drive ",
            "Circle Drive",
            "Clinton Street",
            "George Street",
            "Hillcrest Drive",
            "Hillside Drive",
            "Laurel Street",
            "Park Drive",
            "Penn Street",
            "Railroad Avenue",
            "Riverside Drive",
            "Route 32",
            "Route 6",
            "Sherwood Drive",
            "Summit Street",
            "2nd Street East",
            "6th Street North",
            "Cedar Lane",
            "Creek Road",
            "Durham Road",
            "Elm Avenue",
            "Fairview Avenue",
            "Front Street North",
            "Grant Street",
            "Hamilton Street",
            "Highland Drive",
            "Holly Drive",
            "King Street",
            "Lafayette Avenue",
            "Linden Street",
            "Mulberry Street",
            "Poplar Street",
            "Ridge Avenue",
            "7th Street East",
            "Belmont Avenue",
            "Cambridge Court",
            "Cambridge Drive",
            "Clark Street",
            "College Avenue",
            "Essex Court",
            "Franklin Avenue",
            "Hilltop Road",
            "James Street",
            "Magnolia Drive",
            "Myrtle Avenue",
            "Route 10",
            "Route 29",
            "Shady Lane",
            "Surrey Lane",
            "Walnut Avenue",
            "Warren Street",
            "Williams Street",
            "Wood Street",
            "Woodland Avenue",
    };

    private static final String[] cityNames = {
            "Franklin",
            "Marion",
            "Madison",
            "Springfield",
            "Clinton",
            "Salem",
            "Washington",
            "Hamilton",
            "Jackson",
            "Greenville",
            "Lexington",
            "Troy",
            "Auburn",
            "Hillsboro",
            "Clayton",
    };


    public static String getRandomState() {
        return states[(int) (states.length * Math.random())];
    }

    public static String randomAlphaNumeric(int length) {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < length; ++idx)
            sb.append(alphaNumeric[(int) (alphaNumeric.length * Math.random())]);
        return sb.toString();
    }


    public static String randomDOB() {
        StringBuilder stringBuilder = new StringBuilder();
        int day = (int) (Math.random() * 28);
        int month = (int) (Math.random() * 12);
        int yearDiff = (int) (Math.random() * 12);
        int year = 1994 + yearDiff;

        stringBuilder.append(month < 10 ? "0" + month : month).append("/").append(day < 10 ? "0" + day : day).append("/").append(year);

        return stringBuilder.toString();
    }

    public static String randomExpirationDate(String dob) {
        String[] dobArr = dob.split("/");
        dobArr[2] = "2025";
        return dobArr[0] + "/" + dobArr[1] + "/" + dobArr[2];
    }

    public static String getTopAddress() {
        StringBuilder stringBuilder = new StringBuilder();
        int streetNumber = (int) (Math.random() * 10000);
        stringBuilder.append(streetNumber).append(streetNames[(int) (streetNames.length * Math.random())]);
        return stringBuilder.toString();
    }

    public static String getBottomAddress(String state) {
        return cityNames[(int) (cityNames.length * Math.random())] + ", " + state;
    }

}
