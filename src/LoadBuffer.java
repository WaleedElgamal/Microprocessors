public class LoadBuffer {
    private String iTag;
    private boolean bBusy;
    private int iAddress;

    private static int count=1;

    private int iStartCycle;
    private int iTotalCycles;

    private int iInstructionIndex;

    public LoadBuffer() {
        bBusy=false;
        iTag = "L" + count++;
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
