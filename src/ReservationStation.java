public class ReservationStation {
    private int iTag;
    private boolean bBusy;
    private String strOpcode;
    private int iSourceValue1;
    private int iSourceValue2;
    private int iSourceReg1;
    private int iSourceReg2;
    private int iMemoryAddress;
    private int iCycle;

    public ReservationStation(){
        bBusy=false;
    }

    public int getTag() {
        return iTag;
    }

    public void setTag(int iTag) {
        this.iTag = iTag;
    }

    public boolean isBusy() {
        return bBusy;
    }

    public void setBusy(boolean bBusy) {
        this.bBusy = bBusy;
    }

    public String getOpcode() {
        return strOpcode;
    }

    public void setOpcode(String strOpcode) {
        this.strOpcode = strOpcode;
    }

    public int getSourceValue1() {
        return iSourceValue1;
    }

    public void setSourceValue1(int iSourceValue1) {
        this.iSourceValue1 = iSourceValue1;
    }

    public int getSourceValue2() {
        return iSourceValue2;
    }

    public void setSourceValue2(int iSourceValue2) {
        this.iSourceValue2 = iSourceValue2;
    }

    public int getSourceReg1() {
        return iSourceReg1;
    }

    public void setSourceReg1(int iSourceReg1) {
        this.iSourceReg1 = iSourceReg1;
    }

    public int getSourceReg2() {
        return iSourceReg2;
    }

    public void setSourceReg2(int iSourceReg2) {
        this.iSourceReg2 = iSourceReg2;
    }

    public int getMemoryAddress() {
        return iMemoryAddress;
    }

    public void setMemoryAddress(int iMemoryAddress) {
        this.iMemoryAddress = iMemoryAddress;
    }

    public int getCycle() {
        return iCycle;
    }

    public void setCycle(int iCycle) {
        this.iCycle = iCycle;
    }
}
