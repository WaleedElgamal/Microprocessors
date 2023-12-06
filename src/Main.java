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

    public Main(){
        Cache = new int[10]; //any size and also load with any values

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

    /*private void parseFileIntoMemory(MainMemory memory) throws IOException {
        // todo use correct path for instruction file, use relative src/
        File file = new File("src/test/test");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int i = 0;
        while ((st = br.readLine()) != null) {
            // todo print value after parsing
            String[] stValues = st.split(" ");
            for(int j=0; j<stValues.length; j++){
                stValues[j]=stValues[j].toUpperCase();
            }
            String res = "";
            String opcode = parseOpcode(stValues[0]);
            res += opcode;
            if (stValues[0].equals("J")){
                String temp = Integer.toBinaryString(Integer.parseInt(stValues[1]));
                temp = String.format("%28s", temp).replaceAll(" ", "0");
                res += temp;
            }
            else {
                res += parseRegister(stValues[1]);
                res += parseRegister(stValues[2]);
                if (stValues[0].equals("ADD") || stValues[0].equals("SUB")) { //r type
                    res += parseRegister(stValues[3]); //R3
                    res += "0000000000000";
                } else if (stValues[0].equals("SLL") || stValues[0].equals("SRL")) {
                    res += "00000";
                    String temp = Integer.toBinaryString(Integer.parseInt(stValues[3]));
                    temp = String.format("%13s", temp).replaceAll(" ", "0");
                    res += temp;
                } else { //I type
                    String temp = Integer.toBinaryString(Integer.parseInt(stValues[3]));
                    if(temp.length()>18){
                        temp = temp.substring(temp.length()-18);
                    }
                    else{
                        temp = String.format("%18s", temp).replaceAll(" ", "0");
                    }
                    res += temp;
                }
            }
            memory.setMainMemory(new BigInteger(res, 2).intValue(),i);
            i++;
        }
        numOfInstructions = i; // storing the number of instructions in instruction file
        // will be used to calculate number of clock cycles
    }*/


    public static void main(String[] args) {
        Main Tomasulo = new Main();
        Queue<Instruction> Program = new LinkedList<>();
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

        System.out.println("Choose the number of load buffer");
        int iNumLoadBuffers = sc.nextInt();

        Tomasulo.LoadBuffers = new LoadBuffer[iNumLoadBuffers];
        for (int i = 0; i < iNumLoadBuffers; i++) {
            Tomasulo.LoadBuffers[i] = new LoadBuffer();
        }

        //--------------------------------------------------------------------------------------------------
        System.out.println("Choose the number of store buffer");
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

        int iClockCycle = 1;
        while(iClockCycle<100){ //change later   !Program.isEmpty()
            Instruction curInstruction = Program.poll();
           boolean canIssue = IssueInstruction(curInstruction, Tomasulo);
            HandleAdderStations(Tomasulo);
            HanldeMultiplierStations(Tomasulo);
            HandleLoadBuffers(Tomasulo);
        }
    }




    private static boolean IssueInstruction(Instruction curInstruction, Main Tomasulo){
        boolean canIssue = false;
        if(curInstruction.getType().equals("ADD.D") || curInstruction.getType().equals("SUB.D")){
            for(int i=0; i<Tomasulo.AdderReservationStations.length; i++){
                AdderReservationStation curStation = Tomasulo.AdderReservationStations[i];
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
                        curStation.setSourceValue1(Tomasulo.RegisterFile[iRegister2].getValue());
                    }
                    else{
                        curStation.setSourceReg1(Tomasulo.RegisterFile[iRegister2].getSourceReg());
                    }
                    curStation.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                }
            }
        }
        if(curInstruction.getType().equals("MUL.D") || curInstruction.getType().equals("DIV.D")){
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
                        curStation.setSourceValue1(Tomasulo.RegisterFile[iRegister2].getValue());
                    }
                    else{
                        curStation.setSourceReg1(Tomasulo.RegisterFile[iRegister2].getSourceReg());
                    }
                    curStation.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                }
            }
        }
        if (curInstruction.getType().equals("L.D")){
            for(int i=0; i<Tomasulo.LoadBuffers.length; i++){
                LoadBuffer curBuffer = Tomasulo.LoadBuffers[i];
                if(!curBuffer.isBusy()){
                    canIssue = true;
                    curBuffer.setBusy(true);
                    curBuffer.setAddress(curInstruction.getImmediateValue());
                    curBuffer.setTotalCycles(Tomasulo.hmInstructionCycles.get(curInstruction.getType()));
                }
            }
        }
        if (curInstruction.getType().equals("S.D")){
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
    
    private static void HandleAdderStations(Main Tomasulo) {
        for (int i = 0; i < Tomasulo.AdderReservationStations.length; i++) {
            AdderReservationStation curStation = Tomasulo.AdderReservationStations[i];
            if (curStation.isBusy()) {
                if (curStation.getSourceReg1().equals("") && curStation.getSourceReg2().equals("")) {
                    curStation.setTotalCycles(curStation.getTotalCycles() - 1);
                    if (curStation.getTotalCycles() == 0) {
                        int result = 0;
                        if (curStation.getOpcode().equals("ADD.D")) {
                            result = curStation.getSourceValue1() + curStation.getSourceValue2();
                        } else if (curStation.getOpcode().equals("SUB.D")) {
                            result = curStation.getSourceValue1() - curStation.getSourceValue2();
                        }
                        for (int j = 0; j < Tomasulo.RegisterFile.length; j++){
                            if (Tomasulo.RegisterFile[j].getSourceReg().equals(curStation.getTag())) {
                                Tomasulo.RegisterFile[j].setSourceReg("");
                                Tomasulo.RegisterFile[j].setValue(result);
                            }
                        };
                        for (int j = 0; j < Tomasulo.AdderReservationStations.length; j++) {
                            if (Tomasulo.AdderReservationStations[j].getSourceReg1().equals(curStation.getTag())) {
                                Tomasulo.AdderReservationStations[j].setSourceValue1(result);
                                Tomasulo.AdderReservationStations[j].setSourceReg1("");
                            }
                            if (Tomasulo.AdderReservationStations[j].getSourceReg2().equals(curStation.getTag())) {
                                Tomasulo.AdderReservationStations[j].setSourceValue2(result);
                                Tomasulo.AdderReservationStations[j].setSourceReg2("");
                            }
                        }
                        for (int j = 0; j < Tomasulo.MultiplierReservationStations.length; j++) {
                            if (Tomasulo.MultiplierReservationStations[j].getSourceReg1().equals(curStation.getTag())) {
                                Tomasulo.MultiplierReservationStations[j].setSourceValue1(result);
                                Tomasulo.MultiplierReservationStations[j].setSourceReg1("");
                            }
                            if (Tomasulo.MultiplierReservationStations[j].getSourceReg2().equals(curStation.getTag())) {
                                Tomasulo.MultiplierReservationStations[j].setSourceValue2(result);
                                Tomasulo.MultiplierReservationStations[j].setSourceReg2("");
                            }
                        }
                        curStation.setBusy(false);
                        curStation.setSourceValue1(0);
                        curStation.setSourceValue2(0);
                        curStation.setSourceReg1("");
                        curStation.setSourceReg2("");
                    }
                }
            }
        }
    }

    private static void HanldeMultiplierStations(Main Tomasulo) {
        for (int i = 0; i < Tomasulo.MultiplierReservationStations.length; i++) {
            MultiplierReservationStation curStation = Tomasulo.MultiplierReservationStations[i];
            if (curStation.isBusy()) {
                if (curStation.getSourceReg1().equals("") && curStation.getSourceReg2().equals("")) {
                    curStation.setTotalCycles(curStation.getTotalCycles() - 1);
                    if (curStation.getTotalCycles() == 0) {
                        int result = 0;
                        if (curStation.getOpcode().equals("MUL.D")) {
                            result = curStation.getSourceValue1() * curStation.getSourceValue2();
                        } else if (curStation.getOpcode().equals("DIV.D")) {
                            result = curStation.getSourceValue1() / curStation.getSourceValue2();
                        }
                        for (int j = 0; j < Tomasulo.RegisterFile.length; j++) {
                            if (Tomasulo.RegisterFile[j].getSourceReg().equals(curStation.getTag())) {
                                Tomasulo.RegisterFile[j].setSourceReg("");
                                Tomasulo.RegisterFile[j].setValue(result);
                            }
                        }
                        ;
                        for (int j = 0; j < Tomasulo.AdderReservationStations.length; j++) {
                            if (Tomasulo.AdderReservationStations[j].getSourceReg1().equals(curStation.getTag())) {
                                Tomasulo.AdderReservationStations[j].setSourceValue1(result);
                                Tomasulo.AdderReservationStations[j].setSourceReg1("");
                            }
                            if (Tomasulo.AdderReservationStations[j].getSourceReg2().equals(curStation.getTag())) {
                                Tomasulo.AdderReservationStations[j].setSourceValue2(result);
                                Tomasulo.AdderReservationStations[j].setSourceReg2("");
                            }
                        }
                        for (int j = 0; j < Tomasulo.MultiplierReservationStations.length; j++) {
                            if (Tomasulo.MultiplierReservationStations[j].getSourceReg1().equals(curStation.getTag())) {
                                Tomasulo.MultiplierReservationStations[j].setSourceValue1(result);
                                Tomasulo.MultiplierReservationStations[j].setSourceReg1("");
                            }
                            if (Tomasulo.MultiplierReservationStations[j].getSourceReg2().equals(curStation.getTag())) {
                                Tomasulo.MultiplierReservationStations[j].setSourceValue2(result);
                                Tomasulo.MultiplierReservationStations[j].setSourceReg2("");
                            }
                        }
                        curStation.setBusy(false);
                        curStation.setSourceValue1(0);
                        curStation.setSourceValue2(0);
                        curStation.setSourceReg1("");
                        curStation.setSourceReg2("");
                    }
                }
            }
        }
    }

    private static void HandleLoadBuffers(Main Tomasulo) {

    }
    
}
