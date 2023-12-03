public class MultiplierReservationStation extends ReservationStation{
    private int iMulCycles;
    private int iDivCycles;

    public MultiplierReservationStation(){
        super();
    }

    public int getMulCycles() {
        return iMulCycles;
    }

    public void setMulCycles(int iMulCycles) {
        this.iMulCycles = iMulCycles;
    }

    public int getDivCycles() {
        return iDivCycles;
    }

    public void setDivCycles(int iDivCycles) {
        this.iDivCycles = iDivCycles;
    }
}
