package net.app.civilix;

public class OutflowTable {
    String datetime;
    int outflow;
    int ido;
    String type;
    public OutflowTable(int ido,String datetime,int inflow,String type)
    {
        this.ido=ido;
        this.datetime=datetime;
        this.outflow=inflow;
        this.type=type;
    }
    public int getid() {return ido;}

    public String getdatetime(){return datetime;}

    public int getoutflow(){return outflow;}

    public String gettype(){return type;}
}
