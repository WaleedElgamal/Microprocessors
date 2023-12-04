public class AdderReservationStation extends ReservationStation{
    private static int count=1;

    public AdderReservationStation(){
        super();
        this.setTag("A" + count++);
    }
    

}
