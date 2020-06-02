package utility;

import models.database.DBConnection;

public class IdGenerator {

    private static final String eventIDGen = "EVE";
    private static final String saleIDGen = "SAL";
    private static final String jobIDGen = "JOB";

    public static int getEventIDCount() {
        return DBConnection.getCounter("eventcounter");
    }

    public static int getSaleIDCount() {
        return DBConnection.getCounter("salecounter");
    }

    public static int getJobIDCount() {
        return DBConnection.getCounter("jobcounter");
    }

    public static String generateNewID(String type) {
        String id = "";
        if(type.equalsIgnoreCase("EVE")) {
            id = eventIDGen;
            //Increment the count
            DBConnection.updateCounter("eventcounter");
            int eventIDCount = getEventIDCount();
            int len = String.valueOf(eventIDCount).length();
            if(len == 1)
                id += "00"+eventIDCount;
            else if(len == 2)
                id += "0"+eventIDCount;
            else
                id += ""+eventIDCount;

        }
        else if(type.equalsIgnoreCase("SAL")) {
            id = saleIDGen;
            //Increment the count
            DBConnection.updateCounter("salecounter");
            int saleIDCount = getSaleIDCount();
            int len = String.valueOf(saleIDCount).length();
            if(len == 1)
                id += ("00"+saleIDCount);
            else if(len == 2)
                id += ("0"+saleIDCount);
            else
                id += (""+saleIDCount);
        }
        else if(type.equalsIgnoreCase("JOB")) {
            id = jobIDGen;
            //Increment the count
            DBConnection.updateCounter("jobcounter");
            int jobIDCount = getJobIDCount();
            int len = String.valueOf(jobIDCount).length();
            if(len == 1)
                id += ("00"+jobIDCount);
            else if(len == 2)
                id += ("0"+jobIDCount);
            else
                id += (""+jobIDCount);
        }
        return id;
    }

}
