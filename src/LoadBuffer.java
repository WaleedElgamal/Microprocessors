public class LoadBuffer {
    private int iTag;
    private boolean bBusy;
    private int iAddress;

    public LoadBuffer() {
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

    public int getAddress() {
        return iAddress;
    }

    public void setAddress(int iAddress) {
        this.iAddress = iAddress;
    }
}
