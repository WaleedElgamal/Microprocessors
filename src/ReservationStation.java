public class ReservationStation {
    private String iTag;
    private boolean bBusy;
    private String strOpcode;
    private int iSourceValue1; // Vj
    private int iSourceValue2;// Vk
    private String iSourceReg1; //Qj
    private String iSourceReg2;  //Qk
    private int iMemoryAddress;
    private int iStartCycle;
    private int iTotalCycles;

    public ReservationStation(){
        bBusy=false;
    }

    public String getTag() {
        return iTag;
    }

    public void setTag(String iTag) {
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

    public String getSourceReg1() {
        return iSourceReg1;
    }

    public void setSourceReg1(String iSourceReg1) {
        this.iSourceReg1 = iSourceReg1;
    }

    public String getSourceReg2() {
        return iSourceReg2;
    }

    public void setSourceReg2(String iSourceReg2) {
        this.iSourceReg2 = iSourceReg2;
    }

    public int getMemoryAddress() {
        return iMemoryAddress;
    }

    public void setMemoryAddress(int iMemoryAddress) {
        this.iMemoryAddress = iMemoryAddress;
    }

    public int getStartCycle() {
        return iStartCycle;
    }

    public void setStartCycle(int iStartCycle) {
        this.iStartCycle = iStartCycle;
    }

    public int getTotalCycles() {
        return iTotalCycles;
    }

    public void setTotalCycles(int iTotalCycles) {
        this.iTotalCycles = iTotalCycles;
    }
}
