public class MultiplierReservationStation extends ReservationStation{

    private static int count=1;

    public MultiplierReservationStation(){
        super();
        this.setTag("M" + count++);
    }

}
