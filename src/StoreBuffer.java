public class StoreBuffer {
    private String iTag;
    private boolean bBusy;
    private int iAddress;

    private int iSourceValue;

    private String iSourceReg;

    private static int count = 1;


    private int iTotalCycles;

    public StoreBuffer() {
        bBusy = false;
        iTag = "S" + count++;
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

    public int getSourceValue() {
        return iSourceValue;
    }

    public void setSourceValue(int iSourceValue) {
        this.iSourceValue = iSourceValue;
    }

    public String getSourceReg() {
        return iSourceReg;
    }

    public void setSourceReg(String iSourceReg) {
        this.iSourceReg = iSourceReg;
    }

    public int getTotalCycles() {
        return iTotalCycles;
    }

    public void setTotalCycles(int iTotalCycles) {
        this.iTotalCycles = iTotalCycles;
    }
}