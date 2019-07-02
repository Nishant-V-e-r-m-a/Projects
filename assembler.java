package assembly;
import java.io.*;
import java.util.ArrayList;

class opcode {
	ArrayList<String> name=new ArrayList<String>();
	ArrayList<String> code=new ArrayList<String>();
	public opcode() {
		name.add("CLA");code.add("0000");
		name.add("LAC");code.add("0001");
		name.add("SAC");code.add("0010");
		name.add("ADD");code.add("0011");
		name.add("SUB");code.add("0100");
		name.add("BRZ");code.add("0101");
		name.add("BRN");code.add("0110");
		name.add("BRP");code.add("0111");
		name.add("INP");code.add("1000");
		name.add("DSP");code.add("1001");
		name.add("MUL");code.add("1010");
		name.add("DIV");code.add("1011");
		name.add("STP");code.add("1100");
	}
	public String getopcode(String codenm) {
		String ret="";
		for (int i=0;i<name.size();i++) {
			if (name.get(i).equals(codenm)) {
				ret=code.get(i);
				break;
			}
		}
		return ret;
	}
	public boolean containsopcode(String some) {
		boolean flag=false;
		for (int i=0;i<name.size();i++) {
			if (some.equalsIgnoreCase(name.get(i))) {
				flag=true;
				break;
			}
		}
		return flag;
	}
	
}
class symbolline {
	String name;
	String type;
	String value;
	String address;
	public symbolline(String nam, String typ, String val, String addr) {
		name=nam;
		type=typ;
		value=val;
		address=addr;
	}
}
class symboltable {
	ArrayList<symbolline> symbollnes;
	public symboltable() {
		symbollnes=new ArrayList<symbolline>();
	}
	public void addline(String nam, String typ, String val, String addr) {
		symbolline temp=new symbolline(nam, typ, val, addr);
		symbollnes.add(temp);
	}
}
class literalline {
	String name;
	String value;
	String address;
	public literalline(String nam, String val, String addr) {
		name=nam;
		value=val;
		address=addr;
	}
}
class literaltable {
	ArrayList<literalline> literallnes;
	public literaltable() {
		literallnes=new ArrayList<literalline>();
	}
	public void addline(String nam, String val, String addr) {
		literalline temp=new literalline(nam, val, addr); 
		literallnes.add(temp);
	}
}
class machineline {
	String maddr;
	String mopcode;
	String moperand;
	public machineline(String addr, String opcde, String oprnd) {
		maddr=addr;
		mopcode=opcde;
		moperand=oprnd;
	}
}
class machinetable {
	ArrayList<machineline> machinelnes;
	public machinetable() {
		machinelnes=new ArrayList<machineline>();
	}
	public void addline(String addr, String opcde, String oprnd) {
		machineline temp=new machineline(addr, opcde, oprnd);
		machinelnes.add(temp);
	}
}

public class assembler {
	
	public static String getbinary(int n) {
		String temp="";
		while (n>=2) {
			if (n%2==0) 
				temp="0"+temp;
			else
				temp="1"+temp;
			n/=2;
		}
		if (n==1) {
			temp="1"+temp;
		}
		while (temp.length()<8) {
			temp="0"+temp;
		}
		return temp;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		opcode opcde=new opcode();
		symboltable symboltble=new symboltable();
		literaltable literaltble=new literaltable();
		machinetable machinetble=new machinetable();
		File inputfile=new File("E:\\workspace\\CAOSassembler\\src\\assembly\\input.txt");
		BufferedReader br = new BufferedReader(new FileReader(inputfile));
		BufferedReader ar = new BufferedReader(new FileReader(inputfile));
		BufferedReader cr = new BufferedReader(new FileReader(inputfile));
		
		File outputfile = new File("E:\\workspace\\CAOSassembler\\src\\assembly\\output.txt");
        FileWriter fr = null;
        BufferedWriter xr = null;
        
		String st;
		boolean stop=false;
		String[] line;
		int numline=0;
		int len=0;
		boolean hasstop=false;
		while ((st = ar.readLine()) != null) {
			len++;
		}
		while ((st = br.readLine()) != null && stop==false) {
			line=st.split("\t");
			if (line.length==1 || line.length==0) {
				continue;
			}
			if (line.length>=2) {
				if (line[1].equals("STP")) {
					hasstop=true;
				}
			}
			if (line.length>=3) {
			if (!(line[2].equals(""))) {
				if (line[1].equals("STP") || line[1].equals("CLA")) {
					if (line[1].equals("STP")) {
						hasstop=true;
					}
					continue;
				}
				else {
				boolean flag=false;
				boolean flag2=false;
				if (!(line[2].length()>1 && line[2].charAt(1)=='=')) {
					if (opcde.containsopcode(line[2]) || line[2].equals("DB") || line[2].equals("DC") || line[2].equals("DW") || line[2].equals("DS")) {
						numline++;
						continue;
					}
					for (int i=0;i<symboltble.symbollnes.size();i++) {
						if (line[2].equals(symboltble.symbollnes.get(i).name)) {
							flag=true;
							break;
						}
					}
					if (flag==false) {
						if (opcde.containsopcode(line[1])) {
							symboltble.addline(line[2], "VARIABLE", "NULL", "NULL");
						}
					}
				}
				else {
					for (int i=0;i<literaltble.literallnes.size();i++) {
						if (line[2].equals(literaltble.literallnes.get(i).name)) {
							flag2=true;
							break;
						}
					}
					if (flag2==false) {
						if (opcde.containsopcode(line[1])) {
							literaltble.addline(line[2], line[2].substring(2, line[2].length()-1), getbinary(len));
							len++;
						}
					}
				}
				}
			}}
			if (line[1].equals("DB") || line[1].equals("DC") || line[1].equals("DW") || line[1].equals("DS")) {
				if (line[0].equals("")) {
					if (hasstop==true) {
					System.out.print("Error in line "+(numline+1)+":\t");
					for (int i=0;i<line.length;i++) {
						System.out.print(line[i]+"\t");
					}
					System.out.println();
					System.out.println("Missing symbol name, Ignoring this line..");
					System.out.println();
					}
					numline++;
					continue;
				}
				if (line.length==2 || (line.length>=3 && line[2].equals(""))) {
					if (hasstop==true) {
					System.out.print("Error in line "+(numline+1)+":\t");
					for (int i=0;i<line.length;i++) {
						System.out.print(line[i]+"\t");
					}
					System.out.println();
					System.out.println("Missing symbol value, Ignoring this line..");
					System.out.println();
					}
					numline++;
					continue;
				}
				if (opcde.containsopcode(line[0]) || line[0].equals("DB") || line[0].equals("DC") || line[0].equals("DW") || line[0].equals("DS")) {
					if (hasstop==true) {
					System.out.print("Error in line "+(numline+2)+":\t");
					for (int i=0;i<line.length;i++) {
						System.out.print(line[i]+"\t");
					}
					System.out.println();
					System.out.println(line[0]+" is a reserved keyword, can't use it as a symbol name, Ignoring this line..");
					System.out.println();
					}
					numline++;
					continue;
				}
				boolean flag=false;
				for (int i=0;i<symboltble.symbollnes.size();i++) {
					if (line[0].equals(symboltble.symbollnes.get(i).name)) {
						if (!(symboltble.symbollnes.get(i).address.equals("NULL"))) {
							System.out.print("Error in line "+(numline+2)+":\t");
							for (int s=0;s<line.length;s++) {
								System.out.print(line[s]+"\t");
							}
							System.out.println();
							System.out.println("Symbol "+line[0]+" defined multiple times, either variable/variable or variable/label or label/label");
							System.out.println("Fix it to continue");
							try{
					            fr = new FileWriter(outputfile);
					            xr = new BufferedWriter(fr);
					            xr.write("Fix the ERROR.");
					            xr.flush();
					        } catch (IOException e) {
					            e.printStackTrace();
					        }finally{
					            try {
					                xr.close();
					                fr.close();
					            } catch (IOException e) {
					                e.printStackTrace();
					            }
					        }
							stop=true;
						}
						symboltble.symbollnes.get(i).value=line[2];
						symboltble.symbollnes.get(i).address=getbinary(numline);
						flag=true;
						break;
					}
				}
				if (flag==false) {
					symboltble.addline(line[0], "VARIABLE", line[2], getbinary(numline));
				}	
			}
			if (!(line[0].equals("")) && (line.length>=2) && !(line[1].equals("DB") || line[1].equals("DC") || line[1].equals("DW") || line[1].equals("DS"))) {
				for (int i=0;i<symboltble.symbollnes.size();i++) {
					if (line[0].equals(symboltble.symbollnes.get(i).name)) {
						if (symboltble.symbollnes.get(i).type.equals("LABEL")) {
							System.out.print("Error in line "+(numline+1)+":\t");
							for (int s=0;s<line.length;s++) {
								System.out.print(line[s]+"\t");
							}
							System.out.println();
							System.out.println("Error: Label name "+line[0]+" defined multiple times");
							System.out.println("Fix it to continue");
							try{
					            fr = new FileWriter(outputfile);
					            xr = new BufferedWriter(fr);
					            xr.write("Fix the ERROR.");
					            xr.flush();
					        } catch (IOException e) {
					            e.printStackTrace();
					        }finally{
					            try {
					                xr.close();
					                fr.close();
					            } catch (IOException e) {
					                e.printStackTrace();
					            }
					        }
							stop=true;
							break;
						}
						else {
							System.out.print("Error in line "+(numline+1)+":\t");
							for (int s=0;s<line.length;s++) {
								System.out.print(line[s]+"\t");
							}
							System.out.println();
							System.out.println("Error: This label name has already been used as a variable");
							System.out.println("Fix it to continue");
							try{
					            fr = new FileWriter(outputfile);
					            xr = new BufferedWriter(fr);
					            xr.write("Fix the ERROR.");
					            xr.flush();
					        } catch (IOException e) {
					            e.printStackTrace();
					        }finally{
					            try {
					                xr.close();
					                fr.close();
					            } catch (IOException e) {
					                e.printStackTrace();
					            }
					        }
							stop=true;
							break;
						}
					}
				}
				if (opcde.containsopcode(line[1])) {
					if (opcde.containsopcode(line[0]) || line[0].equals("DB") || line[0].equals("DC") || line[0].equals("DW") || line[0].equals("DS")) {
						numline++;
						continue;
					}
					symboltble.addline(line[0], "LABEL   ", "NULL", getbinary(numline));
				}
			}
			numline++;
		}
		if (hasstop==false && stop==false) {
			System.out.println();
			System.out.println("Error: There is no STOP condition in your program");
			System.out.println("Please insert STP to continue");
			try{
	            fr = new FileWriter(outputfile);
	            xr = new BufferedWriter(fr);
	            xr.write("Fix the ERROR.");
	            xr.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally{
	            try {
	                xr.close();
	                fr.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
			stop=true;
		}
		if (stop==false) {
		int adr=0;
		System.out.println();
		boolean chal=true;
		while ((st = cr.readLine()) != null && chal==true) {
			line=st.split("\t");
			if (line.length==1) {
				if (line[0].equals("") || (line[0].length()>=1 && line[0].charAt(0)=='#')) {
					continue;
				}
				System.out.print("Error in line "+(adr+1)+":\t");
				for (int i=0;i<line.length;i++) {
					System.out.print(line[i]+"\t");
				}
				System.out.println();
				System.out.println("Invalid line, skipping this line..");
				System.out.println();
				continue;
			}
			if (adr==0) {
				if (!(line[1].equals("START"))) {
					System.out.print("Error in line "+(adr+1)+":\t");
					for (int i=0;i<line.length;i++) {
						System.out.print(line[i]+"\t");
					}
					System.out.println();
					System.out.println("Missing START statement, still continuing without START..");
					System.out.println();
				}
			}
			boolean flag=false;
			for (int i=0;i<opcde.name.size();i++) {
				if (opcde.name.get(i).equals(line[1])) {
					flag=true;
					break;
				}
			}
			if (flag==false) {
				if (line[1].equals("DB") || line[1].equals("DC") || line[1].equals("DW") || line[1].equals("DS")) {
					if (opcde.containsopcode(line[0]) || line[0].equals("DB") || line[0].equals("DC") || line[0].equals("DW") || line[0].equals("DS")) {
						System.out.print("Error in line "+(adr+1)+":\t");
						for (int i=0;i<line.length;i++) {
							System.out.print(line[i]+"\t");
						}
						System.out.println();
						System.out.println(line[0]+" is a reserved keyword, cann't use it as a symbol name, Ignoring this line..");
						System.out.println();
						adr++;
						continue;
					}
					if (line[0].equals("")) {
						System.out.print("Error in line "+(adr+1)+":\t");
						for (int i=0;i<line.length;i++) {
							System.out.print(line[i]+"\t");
						}
						System.out.println();
						System.out.println("Missing symbol name, Ignoring this line..");
						System.out.println();
						adr++;
						continue;
					}
					if (line.length==2 || (line.length>=3 && line[2].equals(""))) {
						System.out.print("Error in line "+(adr+1)+":\t");
						for (int i=0;i<line.length;i++) {
							System.out.print(line[i]+"\t");
						}
						System.out.println();
						System.out.println("Missing symbol value, Ignoring this line..");
						System.out.println();
						adr++;
						continue;
					}
//					machinetble.addline(getbinary(adr++), "", "");
					adr++;
					continue;
				}
				System.out.print("Error in line "+(adr+1)+":\t");
				for (int i=0;i<line.length;i++) {
					System.out.print(line[i]+"\t");
				}
				System.out.println();
				if (line[1].equals("")) {
					System.out.println("No opcode present in this line, Ignoring this line..");
				}
				else {
				System.out.println("The opcode "+line[1]+" is invalid, Ignoring this line..");
				}
				System.out.println();
//				machinetble.addline(getbinary(adr++), "", "");
				adr++;
				continue;
			}
			else {
				if (opcde.containsopcode(line[0]) || line[0].equals("DB") || line[0].equals("DC") || line[0].equals("DW") || line[0].equals("DS")) {
					System.out.print("Error in line "+(adr+1)+":\t");
					for (int i=0;i<line.length;i++) {
						System.out.print(line[i]+"\t");
					}
					System.out.println();
					System.out.println(line[0]+" is a reserved keyword, cann't use it as a symbol name, Ignoring this line..");
					System.out.println();
					adr++;
					continue;
				}
				if (line[1].equals("STP") || line[1].equals("CLA")) {
					if (line.length>=3 && line[2].length()>=1) {
						System.out.print("Error in line "+(adr+1)+":\t");
						for (int i=0;i<line.length;i++) {
							System.out.print(line[i]+"\t");
						}
						System.out.println();
						System.out.println("The opcode "+line[1]+" does not require any operand, Ignoring operand in this line..");
						System.out.println();
					}
					machinetble.addline(getbinary(adr++), opcde.getopcode(line[1]), "");
					if (line[1].equals("STP")) {
						chal=false;
					}
					continue;
				}
//				if ()
				if (line.length==2 || line[2].equals("")) {
					System.out.print("Error in line "+(adr+1)+":\t");
					for (int i=0;i<line.length;i++) {
						System.out.print(line[i]+"\t");
					}
					System.out.println();
					System.out.println("Missing operand, Continuing without operand..");
					System.out.println();
					machinetble.addline(getbinary(adr++), opcde.getopcode(line[1]), "");
					continue;
				}
				else {
					String madr="";
					if (!(line[2].length()>1 && line[2].charAt(1)=='=')) {
						if (opcde.containsopcode(line[2]) || line[2].equals("DB") || line[2].equals("DC") || line[2].equals("DW") || line[2].equals("DS")) {
							System.out.print("Error in line "+(adr+1)+":\t");
							for (int i=0;i<line.length;i++) {
								System.out.print(line[i]+"\t");
							}
							System.out.println();
							System.out.println(line[2]+" is a reserved keyword, cann't use it as a symbol name, Ignoring this symbol..");
							System.out.println();
							machinetble.addline(getbinary(adr++), opcde.getopcode(line[1]), "");
							continue;
						}
						boolean flag1=false;
						boolean temp=false;
						for (int k=0;k<symboltble.symbollnes.size();k++) {
							if (symboltble.symbollnes.get(k).name.equals(line[2]) && !(symboltble.symbollnes.get(k).address.equals("NULL"))) {
								if (symboltble.symbollnes.get(k).type.equals("LABEL") && !(line[1].equals("BRZ") || line[1].equals("BRN") || line[1].equals("BRP"))) {
									System.out.print("Error in line "+(adr+1)+":\t");
									for (int i=0;i<line.length;i++) {
										System.out.print(line[i]+"\t");
									}
									System.out.println();
									System.out.println("You entered label name in operand instead of variable or literal, ignoring this label name..");
									System.out.println();
//									machinetble.addline(getbinary(adr++), opcde.getopcode(line[1]), "");
									temp=true;
								}
								flag1=true;
								madr=symboltble.symbollnes.get(k).address;
								if (temp==true) {
									madr="";
								}
								break;
							}
							
							
						}
						if (flag1==false) {
							boolean tocheck=false;
							for (int lt=0;lt<line[2].length();lt++) {
								if (line[2].charAt(lt)==' ') {
									System.out.print("Error in line "+(adr+1)+":\t");
									for (int i=0;i<line.length;i++) {
										System.out.print(line[i]+"\t");
									}
									System.out.println();
									System.out.println("Multiple variables provided, ignoring this operand part..");
									System.out.println();
									machinetble.addline(getbinary(adr++), opcde.getopcode(line[1]), "");
									tocheck=true;
									break;
								}
							}
							if (tocheck==true) {
								continue;
							}
							System.out.print("Error in line "+(adr+1)+":\t");
							for (int i=0;i<line.length;i++) {
								System.out.print(line[i]+"\t");
							}
							System.out.println();
							System.out.println("Undefined operand "+line[2]+", continuing without this operand..");
							System.out.println();
							machinetble.addline(getbinary(adr++), opcde.getopcode(line[1]), "");
							continue;
						}
						else {
							machinetble.addline(getbinary(adr++), opcde.getopcode(line[1]), madr);
							continue;
						}
					}
					else {
						for (int i=0;i<literaltble.literallnes.size();i++) {
							if (literaltble.literallnes.get(i).name.equals(line[2])) {
								madr=literaltble.literallnes.get(i).address;
								break;
							}
						}
						machinetble.addline(getbinary(adr++), opcde.getopcode(line[1]), madr);
						continue;
					}
				}
			}
			
		}
		System.out.println();
		System.out.println("          ---------Symbol Table----------");
		if (symboltble.symbollnes.size()==0) {
			System.out.println("              _______(No Entries)______");
		}
		else {
			System.out.format("%15s%15s%15s%15s", "Address", "Name", "Type", "Value");
			System.out.println();
		for (int j=0;j<symboltble.symbollnes.size();j++) {
			System.out.format("%15s%15s%15s%15s", symboltble.symbollnes.get(j).address, symboltble.symbollnes.get(j).name, symboltble.symbollnes.get(j).type, symboltble.symbollnes.get(j).value);
			System.out.println();
		}}
		System.out.println();
		System.out.println();
		System.out.println("          ---------Literal Table----------");
		if (literaltble.literallnes.size()==0) {
			System.out.println("              _______(No Entries)______");
		}
		else {
		System.out.format("%15s%15s%15s", "Address", "Name", "Value");
		System.out.println();
		for (int j=0;j<literaltble.literallnes.size();j++) {
			System.out.format("%15s%15s%15s", literaltble.literallnes.get(j).address, literaltble.literallnes.get(j).name, literaltble.literallnes.get(j).value);
			System.out.println();
		}}
		System.out.println();
		System.out.println();
		System.out.println("          --------Machine Code-----------");
		if (machinetble.machinelnes.size()==0) {
			System.out.println("              _______(No Entries)______");
		}
		else {
		System.out.format("%15s%15s%15s", "ILC", "Opcode", "Operand");
		System.out.println();
		for (int l=0;l<machinetble.machinelnes.size();l++) {
			System.out.format("%15s%15s%15s", machinetble.machinelnes.get(l).maddr, machinetble.machinelnes.get(l).mopcode, machinetble.machinelnes.get(l).moperand);
			System.out.println();
		}}
		
		try{
            fr = new FileWriter(outputfile);
            xr = new BufferedWriter(fr);
            xr.write("       ---------Symbol Table----------");
            xr.newLine();
            xr.newLine();
            if (symboltble.symbollnes.size()==0) {
    			xr.write("         _______(No Entries)______");
    			xr.newLine();
    			xr.newLine();
    		}
            else {
            	xr.write("Address  \tName\tType    \tValue");
            	xr.newLine();
            for (int j=0;j<symboltble.symbollnes.size();j++) {
//    			System.out.format("%15s%15s%15s%15s", symboltble.symbollnes.get(j).address, symboltble.symbollnes.get(j).name, symboltble.symbollnes.get(j).type, symboltble.symbollnes.get(j).value);
//    			System.out.println();
            	xr.write(symboltble.symbollnes.get(j).address+"\t"+symboltble.symbollnes.get(j).name+"\t"+symboltble.symbollnes.get(j).type+"\t"+symboltble.symbollnes.get(j).value);
            	xr.newLine();
    		}}
            xr.newLine();
            xr.newLine();
            xr.write("       ---------Literal Table----------");
            xr.newLine();
            xr.newLine();
    		if (literaltble.literallnes.size()==0) {
    			xr.write("         _______(No Entries)______");
    			xr.newLine();
                xr.newLine();
    		}
    		else {
    		xr.write("Address \tName\tValue");
    		xr.newLine();
    		for (int j=0;j<literaltble.literallnes.size();j++) {
    			xr.write(literaltble.literallnes.get(j).address+"\t"+literaltble.literallnes.get(j).name+"\t"+literaltble.literallnes.get(j).value);
    			xr.newLine();
    		}}
    		xr.newLine();
    		xr.newLine();
    		xr.write("       --------Machine Code-----------");
    		xr.newLine();
            xr.newLine();
    		if (machinetble.machinelnes.size()==0) {
    			xr.write("       _______(No Entries)______");
    			xr.newLine();
                xr.newLine();
    		}
    		else {
    		xr.write("ILC     \tOpcode\tOperand");
    		xr.newLine();
    		for (int l=0;l<machinetble.machinelnes.size();l++) {
    			xr.write(machinetble.machinelnes.get(l).maddr+"\t"+machinetble.machinelnes.get(l).mopcode+"\t"+machinetble.machinelnes.get(l).moperand);
    			xr.newLine();
    		}}
            xr.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                xr.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	}

}
