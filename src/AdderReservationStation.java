public class AdderReservationStation extends ReservationStation{
    private int iAddCycles;
    private int iSubCycles;

    public AdderReservationStation(){
        super();
    }

    public int getAddCycles() {
        return iAddCycles;
    }

    public void setAddCycles(int iAddCycles) {
        this.iAddCycles = iAddCycles;
    }

    public int getSubCycles() {
        return iSubCycles;
    }

    public void setSubCycles(int iSubCycles) {
        this.iSubCycles = iSubCycles;
    }
}
