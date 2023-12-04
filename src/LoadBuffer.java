public class LoadBuffer {
    private String iTag;
    private boolean bBusy;
    private int iAddress;

    private static int count=1;

    private int iTotalCycles;



    public LoadBuffer() {
        bBusy=false;
        iTag = "L" + count++;
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

    public int getTotalCycles() {
        return iTotalCycles;
    }
    public void setTotalCycles(int iTotalCycles) {
        this.iTotalCycles = iTotalCycles;
    }
}
