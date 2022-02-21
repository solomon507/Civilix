package net.app.civilix;

public class InflowTable {
    String datetime;
    int inflow;
    int id;
    String type;
    public InflowTable(int id,String datetime,int inflow,String type)
    {
        this.id=id;
        this.datetime=datetime;
        this.inflow=inflow;
        this.type=type;
    }
    public int getid(){return id;}

    public String getdatetime(){return datetime;}

    public int getinflow(){return inflow;}

    public String gettype(){return type;}
}
