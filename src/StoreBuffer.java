public class StoreBuffer {
    private String iTag;
    private boolean bBusy;
    private int iAddress;

    private double iSourceValue; // V

    private String iSourceReg; //Q

    private static int count = 1;

    private int iStartCycle;
    private int iTotalCycles;

    private int iInstructionIndex;

    public StoreBuffer() {
        bBusy = false;
        iTag = "S" + count++;
    }

    public String toString(){
        return iTag + " " + bBusy + "  " + iAddress;
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

    public int getAddress() {
        return iAddress;
    }

    public void setAddress(int iAddress) {
        this.iAddress = iAddress;
    }

    public double getSourceValue() {
        return iSourceValue;
    }

    public void setSourceValue(double iSourceValue) {
        this.iSourceValue = iSourceValue;
    }

    public String getSourceReg() {
        return iSourceReg;
    }

    public void setSourceReg(String iSourceReg) {
        this.iSourceReg = iSourceReg;
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

    public int getInstructionIndex() {
        return iInstructionIndex;
    }

    public void setInstructionIndex(int iInstructionIndex) {
        this.iInstructionIndex = iInstructionIndex;
    }
}