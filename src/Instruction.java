public class Instruction {
    private String strType;
    private int iDestinationRegister;
    private int iSourceRegister1;
    private int iSourceRegister2;
    private int iImmediateValue;

    public Instruction(){

    }

    public String getType() {
        return strType;
    }

    public void setType(String strType) {
        this.strType = strType;
    }

    public int getDestinationRegister() {
        return iDestinationRegister;
    }

    public void setDestinationRegister(int iDestinationRegister) {
        this.iDestinationRegister = iDestinationRegister;
    }

    public int getSourceRegister1() {
        return iSourceRegister1;
    }

    public void setSourceRegister1(int iSourceRegister1) {
        this.iSourceRegister1 = iSourceRegister1;
    }

    public int getSourceRegister2() {
        return iSourceRegister2;
    }

    public void setSourceRegister2(int iSourceRegister2) {
        this.iSourceRegister2 = iSourceRegister2;
    }

    public int getImmediateValue() {
        return iImmediateValue;
    }

    public void setImmediateValue(int iImmediateValue) {
        this.iImmediateValue = iImmediateValue;
    }
}
