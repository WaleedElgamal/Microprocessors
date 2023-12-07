import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
public class Main {
    private AdderReservationStation[] AdderReservationStations;
    private MultiplierReservationStation[] MultiplierReservationStations;
    private Register[] RegisterFile;
    private int[] Cache;
    private LoadBuffer[] LoadBuffers;
    private StoreBuffer[] StoreBuffers;
    private HashMap<String, Integer> hmInstructionCycles = new HashMap<>();

    static int iClockCycle = 1;
    private Queue<String> WriteResult = new LinkedList<>();

    //todo add load and store in write result
    public Main(){
        Cache = new int[10]; //any size and also load with any values
        RegisterFile = new Register[10];
        for(int i=0; i<10; i++){
            RegisterFile[i] = new Register(i, i);
        }

        hmInstructionCycles.put("ADD.D",-1);
        hmInstructionCycles.put("SUB.D",-1);
        hmInstructionCycles.put("MUL.D",-1);
        hmInstructionCycles.put("DIV.D",-1);

        hmInstructionCycles.put("DADDI",1); //specified
        hmInstructionCycles.put("DSUBI",-1); //should be one as well?

        hmInstructionCycles.put("L.D",-1);
        hmInstructionCycles.put("S.D",-1);

        hmInstructionCycles.put("BNEZ",1); //specified
    }

    public static void ParseProgram(ArrayList<Instruction> Program) throws IOException {
        // todo use correct path for instruction file, use relative src/
        File file = new File("src/Program.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int i = 0;
        while ((st = br.readLine()) != null) {
            // todo print value after parsing
            String[] stValues = st.split(" ");
            for(int j=0; j<stValues.length; j++){
                stValues[j]=stValues[j].toUpperCase();
            }
            Instruction newInstruction = new Instruction();
            String type = stValues[0];
            int dest =  Integer.parseInt(stValues[1].substring(1));;
            if(type.equals("ADD.D") || type.equals("SUB.D") || type.equals("MUL.D") || type.equals("DIV.D") ) {
                int source1 = Integer.parseInt(stValues[2].substring(1));
                int source2 =  Integer.parseInt(stValues[3].substring(1));
                newInstruction = new Instruction(type, dest, source1, source2);
            }
            else if (type.equals("L.D") || type.equals("S.D") || type.equals("BNEZ")){
                int immediate = Integer.parseInt(stValues[2]);
                newInstruction = new Instruction(type, dest, immediate);
            }
            else if (type.equals("DADDI") || type.equals("DSUBI")){
                int source1 =  Integer.parseInt(stValues[2].substring(1));
                int immediate = Integer.parseInt(stValues[3]);
                newInstruction = new Instruction(type, dest, source1, immediate);
            }
            Program.add(newInstruction);
            //System.out.println(newInstruction);
        }
        //numOfInstructions = i; // storing the number of instructions in instruction file
        // will be used to calculate number of clock cycles
    }


    public static void main(String[] args) throws IOException{
        Main Tomasulo = new Main();
        ArrayList<Instruction> Program = new ArrayList<>();
        ParseProgram(Program);
        HashSet<Integer> Executing = new HashSet<>();

        Scanner sc = new Scanner(System.in);

        //todo initialize register file

        System.out.println("Choose the number of adder reservation stations");
        int iNumAdderStations = sc.nextInt();
        Tomasulo.AdderReservationStations = new AdderReservationStation[iNumAdderStations];
        for (int i = 0; i < iNumAdderStations; i++) {
            Tomasulo.AdderReservationStations[i] = new AdderReservationStation();
        }

        System.out.println("Choose the number of multiplier reservation stations");
        int iNumMultiplierStations = sc.nextInt();
        Tomasulo.MultiplierReservationStations = new MultiplierReservationStation[iNumMultiplierStations];
        for (int i = 0; i < iNumMultiplierStations; i++) {
            Tomasulo.MultiplierReservationStations[i] = new MultiplierReservationStation();
        }

        //--------------------------------------------------------------------------------------------------

        System.out.println("Choose the number of load buffers");
        int iNumLoadBuffers = sc.nextInt();

        Tomasulo.LoadBuffers = new LoadBuffer[iNumLoadBuffers];
        for (int i = 0; i < iNumLoadBuffers; i++) {
            Tomasulo.LoadBuffers[i] = new LoadBuffer();
        }

        //--------------------------------------------------------------------------------------------------
        System.out.println("Choose the number of store buffers");
        int iNumStoreBuffers = sc.nextInt();

        Tomasulo.StoreBuffers = new StoreBuffer[iNumStoreBuffers];
        for (int i = 0; i < iNumStoreBuffers; i++) {
            Tomasulo.StoreBuffers[i] = new StoreBuffer();
        }

        //--------------------------------------------------------------------------------------------------




        for (String strInstruction : Tomasulo.hmInstructionCycles.keySet()) {
            if (!strInstruction.equals("DADDI") && !strInstruction.equals("BNEZ")) {
                System.out.println("Choose the latency of " + strInstruction + " instruction:");
                int iLatency = sc.nextInt();
                Tomasulo.hmInstructionCycles.replace(strInstruction, iLatency);
            }
        }

        //--------------------------------------------------------------------------------------------------


        int i =0;
        while(iClockCycle<=4){ //change later   !Program.isEmpty()
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("Cycle " + iClockCycle + ":" + "\n");

            Instruction curInstruction = Program.get(i);
            boolean canIssue = IssueInstruction(curInstruction, Tomasulo, i);
            HandleAdderStations(Tomasulo, Executing);
            HandleMultiplierStations(Tomasulo, Executing);
            HandleLoadBuffers(Tomasulo, Executing);
            //todo handlestorebuffers
            int writesResult = HandleWriteResult(Tomasulo, Executing);

            Display(Tomasulo, Program, writesResult, canIssue, i, Executing);
            if(canIssue)
                i++;
            iClockCycle++;
        }
    }

    private static void Display(Main Tomasulo, ArrayList<Instruction> Program, int InstructionIndex, boolean issued, int index, HashSet<Integer> Executing) {
        System.out.println("Issued instruction: " );
        if(issued){
            System.out.println(Program.get(index).toString() + "\n");
        }
        else{
            System.out.println("No empty reservation station to issue instruction \n");
        }

        //--------------------------------------------------------------------------------------------------

        System.out.println("Executing instructions: " );
        for (Integer x: Executing) {
            System.out.println(Program.get(x).toString());
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Writes result: " );
        if(InstructionIndex!=-1){
            System.out.println(Program.get(InstructionIndex).toString() + "\n" );
        }
        else{
            System.out.println("No instruction writes its result\n");
        }

        //--------------------------------------------------------------------------------------------------

        System.out.println("Load Buffers: " );
        System.out.println("   Busy   Address");
        for(int i=0; i<Tomasulo.LoadBuffers.length; i++){
            System.out.println(Tomasulo.LoadBuffers[i]);
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Store Buffers: " );
        System.out.println("   Busy   Address");
        for(int i=0; i<Tomasulo.StoreBuffers.length; i++){
            System.out.println(Tomasulo.StoreBuffers[i]);
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Adder Reservation Stations: " );
        System.out.println("   Busy   Op    Vi    Vk    Qi   Qk  A");
        for(int i=0; i<Tomasulo.AdderReservationStations.length; i++){
            System.out.println(Tomasulo.AdderReservationStations[i]);
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Multiplier Reservation Stations: " );
        System.out.println("   Busy   Op    Vi    Vk    Qi   Qk  A");
        for(int i=0; i<Tomasulo.MultiplierReservationStations.length; i++){
            System.out.println(Tomasulo.MultiplierReservationStations[i]);
        }
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Cache: " );
        System.out.println(Arrays.toString(Tomasulo.Cache));
        System.out.println();

        //--------------------------------------------------------------------------------------------------

        System.out.println("Register File: " );
        for(int i=0; i<Tomasulo.RegisterFile.length; i++){
            System.out.println(Tomasulo.RegisterFile[i]);
        }
        System.out.println();
    }


    private static boolean IssueInstruction(Instruction curInstruction, Main Tomasulo, int index){
        boolean canIssue = false;
        if(curInstruction.getType().equals("ADD.D") || curInstruction.getType().equals("SUB.D")){
            for(int i=0; i<Tomasulo.AdderReservationStations.length; i++){
                AdderReservationStation curStation = Tomasulo.AdderReservationStations[i];
                if(!curStation.isBusy()){
                    canIssue = true;
                    curStation.setBusy(true);
                    curStation.setOpcode(curInstruction.getType());
                    curStation.setInstructionIndex(index);
                    int iRegister1 = curInstruction.getSourceRegister1();
                    int iRegister2 = curInstruction.getSourceRegister2();
                    if(Tomasulo.RegisterFile[iRegister1].getSourceReg().equals("")){
                        curStation.setSourceValue1(Tomasulo.RegisterFile[iRegister1].getValue());
                    }
                    else{
                        curStation.setSourceReg1(Tomasulo.RegisterFile[iRegister1].getSourceReg());
                    }
                    if(Tomasulo.RegisterFile[iRegister2].getSourceReg().equals("")){
                        curStation.setSourceValue2(Tomasulo.RegisterFile[iRegister2].getValue());
                    }
                    else{
                        curStation.setSourceReg2(Tomasulo.RegisterFile[iRegister2].getSourceReg());
                    }
                    curStation.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                    curStation.setStartCycle(iClockCycle);
                    break;
                }
            }
        }
        else if(curInstruction.getType().equals("MUL.D") || curInstruction.getType().equals("DIV.D")){
            for(int i=0; i<Tomasulo.MultiplierReservationStations.length; i++){
                MultiplierReservationStation curStation = Tomasulo.MultiplierReservationStations[i];
                if(!curStation.isBusy()){
                    canIssue = true;
                    curStation.setBusy(true);
                    curStation.setOpcode(curInstruction.getType());
                    int iRegister1 = curInstruction.getSourceRegister1();
                    int iRegister2 = curInstruction.getSourceRegister2();
                    if(Tomasulo.RegisterFile[iRegister1].getSourceReg().equals("")){
                        curStation.setSourceValue1(Tomasulo.RegisterFile[iRegister1].getValue());
                    }
                    else{
                        curStation.setSourceReg1(Tomasulo.RegisterFile[iRegister1].getSourceReg());
                    }
                    if(Tomasulo.RegisterFile[iRegister2].getSourceReg().equals("")){
                        curStation.setSourceValue2(Tomasulo.RegisterFile[iRegister2].getValue());
                    }
                    else{
                        curStation.setSourceReg2(Tomasulo.RegisterFile[iRegister2].getSourceReg());
                    }
                    curStation.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                    curStation.setStartCycle(iClockCycle);
                    break;
                }
            }
        }
        else if (curInstruction.getType().equals("L.D")){
            for(int i=0; i<Tomasulo.LoadBuffers.length; i++){
                LoadBuffer curBuffer = Tomasulo.LoadBuffers[i];
                if(!curBuffer.isBusy()){
                    canIssue = true;
                    curBuffer.setBusy(true);
                    curBuffer.setAddress(curInstruction.getImmediateValue());
                    curBuffer.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                    curBuffer.setStartCycle(iClockCycle);
                    break;
                }
            }
        }
        else if (curInstruction.getType().equals("S.D")){
            for(int i=0; i<Tomasulo.StoreBuffers.length; i++){
                StoreBuffer curBuffer = Tomasulo.StoreBuffers[i];
                if(!curBuffer.isBusy()){
                    canIssue = true;
                    curBuffer.setBusy(true);
                    curBuffer.setAddress(curInstruction.getImmediateValue());
                    int iRegister1 = curInstruction.getSourceRegister1();
                    if (Tomasulo.RegisterFile[iRegister1].getSourceReg().equals("")){
                        curBuffer.setSourceValue(Tomasulo.RegisterFile[iRegister1].getValue());
                    }
                    else{
                        curBuffer.setSourceReg(Tomasulo.RegisterFile[iRegister1].getSourceReg());
                    }
                    curBuffer.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                    curBuffer.setStartCycle(iClockCycle);
                    break;
                }
            }
        }
        
        if (curInstruction.getType().equals("DADDI") || curInstruction.getType().equals("DSUBI") || curInstruction.getType().equals("BNEZ")){
            for(int i=0; i<Tomasulo.AdderReservationStations.length; i++){
                AdderReservationStation curStation = Tomasulo.AdderReservationStations[i];
                if(!curStation.isBusy()){
                    canIssue = true;
                    curStation.setBusy(true);
                    curStation.setOpcode(curInstruction.getType());
                    int iRegister1 = curInstruction.getSourceRegister1();
                    if(Tomasulo.RegisterFile[iRegister1].getSourceReg().equals("")){
                        curStation.setSourceValue1(Tomasulo.RegisterFile[iRegister1].getValue());
                    }
                    else{
                        curStation.setSourceReg1(Tomasulo.RegisterFile[iRegister1].getSourceReg());
                    }
                    curStation.setSourceValue2(curInstruction.getImmediateValue());
                    curStation.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                }
            }
        }




        return canIssue;
    }
    
    private static void HandleAdderStations(Main Tomasulo, HashSet<Integer> Executing) {
        for (int i = 0; i < Tomasulo.AdderReservationStations.length; i++) {
            AdderReservationStation curStation = Tomasulo.AdderReservationStations[i];
            if (curStation.isBusy() && curStation.getStartCycle()!= iClockCycle) {
                if (curStation.getSourceReg1().equals("") && curStation.getSourceReg2().equals("")) {
                    curStation.setTotalCycles(curStation.getTotalCycles() - 1);
                    if(!Executing.contains(curStation.getInstructionIndex()));
                        Executing.add(curStation.getInstructionIndex());
                    // changed condition to -1, write happens after the last cycle of execution and not
                    // in the same cycle
                    if (curStation.getTotalCycles() == -1) {
                        Tomasulo.WriteResult.add(curStation.getTag());
//                        int result = 0;
//                        if (curStation.getOpcode().equals("ADD.D")) {
//                            result = curStation.getSourceValue1() + curStation.getSourceValue2();
//                        } else if (curStation.getOpcode().equals("SUB.D")) {
//                            result = curStation.getSourceValue1() - curStation.getSourceValue2();
//                        }
//                        for (int j = 0; j < Tomasulo.RegisterFile.length; j++){
//                            if (Tomasulo.RegisterFile[j].getSourceReg().equals(curStation.getTag())) {
//                                Tomasulo.RegisterFile[j].setSourceReg("");
//                                Tomasulo.RegisterFile[j].setValue(result);
//                            }
//                        }
//                        for (int j = 0; j < Tomasulo.AdderReservationStations.length; j++) {
//                            if (Tomasulo.AdderReservationStations[j].getSourceReg1().equals(curStation.getTag())) {
//                                Tomasulo.AdderReservationStations[j].setSourceValue1(result);
//                                Tomasulo.AdderReservationStations[j].setSourceReg1("");
//                            }
//                            if (Tomasulo.AdderReservationStations[j].getSourceReg2().equals(curStation.getTag())) {
//                                Tomasulo.AdderReservationStations[j].setSourceValue2(result);
//                                Tomasulo.AdderReservationStations[j].setSourceReg2("");
//                            }
//                        }
//                        for (int j = 0; j < Tomasulo.MultiplierReservationStations.length; j++) {
//                            if (Tomasulo.MultiplierReservationStations[j].getSourceReg1().equals(curStation.getTag())) {
//                                Tomasulo.MultiplierReservationStations[j].setSourceValue1(result);
//                                Tomasulo.MultiplierReservationStations[j].setSourceReg1("");
//                            }
//                            if (Tomasulo.MultiplierReservationStations[j].getSourceReg2().equals(curStation.getTag())) {
//                                Tomasulo.MultiplierReservationStations[j].setSourceValue2(result);
//                                Tomasulo.MultiplierReservationStations[j].setSourceReg2("");
//                            }
//                        }
//                        curStation.setBusy(false);
//                        curStation.setSourceValue1(0);
//                        curStation.setSourceValue2(0);
//                        curStation.setSourceReg1("");
//                        curStation.setSourceReg2("");
                    }
                }
            }
        }
    }

    private static void HandleMultiplierStations(Main Tomasulo, HashSet<Integer> Executing) {
        for (int i = 0; i < Tomasulo.MultiplierReservationStations.length; i++) {
            MultiplierReservationStation curStation = Tomasulo.MultiplierReservationStations[i];
            if (curStation.isBusy() && curStation.getStartCycle()!= iClockCycle) {
                if (curStation.getSourceReg1().equals("") && curStation.getSourceReg2().equals("")) {
                    if(!Executing.contains(curStation.getInstructionIndex()));
                        Executing.add(curStation.getInstructionIndex());
                    curStation.setTotalCycles(curStation.getTotalCycles() - 1);
                    if (curStation.getTotalCycles() == -1) {
                        Tomasulo.WriteResult.add(curStation.getTag());
//                        int result = 0;
//                        if (curStation.getOpcode().equals("MUL.D")) {
//                            result = curStation.getSourceValue1() * curStation.getSourceValue2();
//                        } else if (curStation.getOpcode().equals("DIV.D")) {
//                            result = curStation.getSourceValue1() / curStation.getSourceValue2();
//                        }
//                        for (int j = 0; j < Tomasulo.RegisterFile.length; j++) {
//                            if (Tomasulo.RegisterFile[j].getSourceReg().equals(curStation.getTag())) {
//                                Tomasulo.RegisterFile[j].setSourceReg("");
//                                Tomasulo.RegisterFile[j].setValue(result);
//                            }
//                        }
//                        for (int j = 0; j < Tomasulo.AdderReservationStations.length; j++) {
//                            if (Tomasulo.AdderReservationStations[j].getSourceReg1().equals(curStation.getTag())) {
//                                Tomasulo.AdderReservationStations[j].setSourceValue1(result);
//                                Tomasulo.AdderReservationStations[j].setSourceReg1("");
//                            }
//                            if (Tomasulo.AdderReservationStations[j].getSourceReg2().equals(curStation.getTag())) {
//                                Tomasulo.AdderReservationStations[j].setSourceValue2(result);
//                                Tomasulo.AdderReservationStations[j].setSourceReg2("");
//                            }
//                        }
//                        for (int j = 0; j < Tomasulo.MultiplierReservationStations.length; j++) {
//                            if (Tomasulo.MultiplierReservationStations[j].getSourceReg1().equals(curStation.getTag())) {
//                                Tomasulo.MultiplierReservationStations[j].setSourceValue1(result);
//                                Tomasulo.MultiplierReservationStations[j].setSourceReg1("");
//                            }
//                            if (Tomasulo.MultiplierReservationStations[j].getSourceReg2().equals(curStation.getTag())) {
//                                Tomasulo.MultiplierReservationStations[j].setSourceValue2(result);
//                                Tomasulo.MultiplierReservationStations[j].setSourceReg2("");
//                            }
//                        }
//                        curStation.setBusy(false);
//                        curStation.setSourceValue1(0);
//                        curStation.setSourceValue2(0);
//                        curStation.setSourceReg1("");
//                        curStation.setSourceReg2("");
                    }
                }
            }
        }
    }

    private static void HandleLoadBuffers(Main Tomasulo, HashSet<Integer> Executing) {

    }


    private static int HandleWriteResult(Main Tomasulo, HashSet<Integer> Executing) {
        int iInstructionIndex=-1;
        if(!Tomasulo.WriteResult.isEmpty()){
            String strTag = Tomasulo.WriteResult.poll();
            char cStation = strTag.charAt(0);
            int result=0;

            switch (cStation){
                case 'A':
                {
                    AdderReservationStation curStation = Tomasulo.AdderReservationStations[Integer.parseInt(strTag.substring(1))-0];
                    iInstructionIndex= curStation.getInstructionIndex();
                    if (curStation.getOpcode().equals("ADD.D")) {
                        result = curStation.getSourceValue1() + curStation.getSourceValue2();
                    } else if (curStation.getOpcode().equals("SUB.D")) {
                        result = curStation.getSourceValue1() - curStation.getSourceValue2();
                    }
                    curStation.setBusy(false);
                    curStation.setSourceValue1(0);
                    curStation.setSourceValue2(0);
                    curStation.setSourceReg1("");
                    curStation.setSourceReg2("");
                    curStation.setInstructionIndex(-1);
                }
                break;
                case 'M':
                {
                    MultiplierReservationStation curStation = Tomasulo.MultiplierReservationStations[Integer.parseInt(strTag.substring(1))-0];
                    iInstructionIndex= curStation.getInstructionIndex();
                    if (curStation.getOpcode().equals("MUL.D")) {
                        result = curStation.getSourceValue1() * curStation.getSourceValue2();
                    } else if (curStation.getOpcode().equals("DIV.D")) {
                        result = curStation.getSourceValue1() / curStation.getSourceValue2();
                    }
                    curStation.setBusy(false);
                    curStation.setSourceValue1(0);
                    curStation.setSourceValue2(0);
                    curStation.setSourceReg1("");
                    curStation.setSourceReg2("");
                    curStation.setInstructionIndex(-1);
                }
                break;
                case 'L':
                {
                }
                break;
                default: //case 'S'
                {

                }
            }

            for (int j = 0; j < Tomasulo.RegisterFile.length; j++) {
                if (Tomasulo.RegisterFile[j].getSourceReg().equals(strTag)) {
                    Tomasulo.RegisterFile[j].setSourceReg("");
                    Tomasulo.RegisterFile[j].setValue(result);
                }
            }
            for (int j = 0; j < Tomasulo.AdderReservationStations.length; j++) {
                if (Tomasulo.AdderReservationStations[j].getSourceReg1().equals(strTag)) {
                    Tomasulo.AdderReservationStations[j].setSourceValue1(result);
                    Tomasulo.AdderReservationStations[j].setSourceReg1("");
                }
                if (Tomasulo.AdderReservationStations[j].getSourceReg2().equals(strTag)) {
                    Tomasulo.AdderReservationStations[j].setSourceValue2(result);
                    Tomasulo.AdderReservationStations[j].setSourceReg2("");
                }
            }
            for (int j = 0; j < Tomasulo.MultiplierReservationStations.length; j++) {
                if (Tomasulo.MultiplierReservationStations[j].getSourceReg1().equals(strTag)) {
                    Tomasulo.MultiplierReservationStations[j].setSourceValue1(result);
                    Tomasulo.MultiplierReservationStations[j].setSourceReg1("");
                }
                if (Tomasulo.MultiplierReservationStations[j].getSourceReg2().equals(strTag)) {
                    Tomasulo.MultiplierReservationStations[j].setSourceValue2(result);
                    Tomasulo.MultiplierReservationStations[j].setSourceReg2("");
                }
            }
            Executing.remove((Integer) iInstructionIndex);
        }
        return iInstructionIndex;
    }
    
}
