public class Register {
    private int iReg;
    private String iSourceReg; //Q
    private int iValue;
    public Register(int iReg, int iValue){
        this.iReg = iReg;
        this.iValue = iValue;
        this.iSourceReg = "";
    }

    public String toString(){
        String str = "F" + iReg;
        if (iSourceReg.equals(""))
            str += " " + iValue;
        else
            str += " " + iSourceReg;
        return str;
    }

    public String getSourceReg() {
        return iSourceReg;
    }

    public void setSourceReg(String iSourceReg) {
        this.iSourceReg = iSourceReg;
    }

    public int getValue() {
        return iValue;
    }

    public void setValue(int iValue) {
        this.iValue = iValue;
    }
}
