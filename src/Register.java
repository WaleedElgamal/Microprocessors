public class Register {
    private int iReg;
    private int iSourceReg; //Q
    private int iValue;
    public Register(int iReg, int iValue){
        this.iReg = iReg;
        this.iValue = iValue;
    }

    public int getSourceReg() {
        return iSourceReg;
    }

    public void setSourceReg(int iSourceReg) {
        this.iSourceReg = iSourceReg;
    }

    public int getValue() {
        return iValue;
    }

    public void setValue(int iValue) {
        this.iValue = iValue;
    }
}
