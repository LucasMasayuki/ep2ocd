package ep2ocd;
import java.util.HashMap;
import java.util.Map;

class Uc {
    private String pc = "0";
    private Palavra ir;
    private Processo mbr;
    private String mar = "0";
    private String ax = "0";
    private String bx = "0";
    private String cx = "0";
    private String dx = "0";
    private int flagE = 0;
    private int flagNe = 0;
    private int flagG = 0;
    private int flagGe = 0;
    private int flagL = 0;
    private int flagLe = 0;
    private Map<String, String> registradoresUc =  new HashMap<String, String>();
    private String res = "";

    private String Registradores[] = {
        "ax",
        "bx",
        "cx",
        "dx"
    };

    private Ula ula = new Ula();

    public Uc() {
        for (int i = 0; i < this.Registradores.length; i++) {
            this.registradoresUc.put(this.Registradores[i], criaComandoBinario(i + 1));
        }
    }
    
    public Palavra getIr() {
    	return this.ir;
    }

    private String criaComandoBinario(int decimal) {
        return Integer.toString(decimal, 2);
    }

    public String getComandoBinario(String componente) {
        return this.registradoresUc.get(componente);
    }

    public boolean verificaSeUmRegistradorValido(String supostoRegistrador) {
        for (int i = 0; i < this.Registradores.length; i++) {
            if (this.Registradores[i].equals(supostoRegistrador)) {
                return true;
            }
        }
        return false;
    }
    
    public int getIndex(String comando) {
    	return 1;
    }

    public Object[][] cicloDeBusca(Firmware firmware, Memoria memoria, int atual) {
    	String[] sinais = firmware.getSinaisDeControle(0, this.ir);
    	for (int jota = 0; jota < sinais[atual].length(); jota++) {
	    	if (jota == 1 && sinais[atual].charAt(jota) == '1' && sinais[atual].charAt(jota + 1) == '1') {
    			this.mar = this.pc;
    		}
	    	if (jota == 19 && sinais[atual].charAt(jota) == '1') {
    			StringBuilder binario = new StringBuilder();
    			binario.append(sinais[atual].charAt(26));
    			binario.append(sinais[atual].charAt(27));
    			binario.append(sinais[atual].charAt(28));
    	    	res = binario.toString();
    			ula.setX(Integer.toBinaryString(1));
    			ula.setY(pc);
    		}
	    	if (jota == 0 && sinais[atual].charAt(jota) == '1' && sinais[atual].charAt(19) == '1') {
    			this.pc = Integer.toHexString(ula.getResultado(res));
    		}
	
	    	if (jota == 21 && sinais[atual].charAt(jota) == '1' && sinais[atual].charAt(24) == '1') {
    			memoria.setEnderecoTemporario(this.mar);
    		} 
	    	if (jota == 23 && sinais[atual].charAt(jota) == '1' && sinais[atual].charAt(25) == '1') {
    			this.mbr = memoria.getProcesso(memoria.getEnderecoTemporario());
    		}
	    	if (jota == 4 && sinais[atual].charAt(jota) == '1' && sinais[atual].charAt(15) == '1' && sinais[atual].charAt(17) == '1') {
    			this.ir = (Palavra) this.mbr.dados;
    		}
		}

    	String binarioIr;
    	String binarioMbr;

    	if (this.ir == null) {
    		binarioIr = "0";
    	} else {
    		binarioIr = this.ir.getPalavraCompleta();
    	}
    	
    	if (this.mbr == null) {
    		binarioMbr = "0";
    	} else {
    		Palavra palavra = (Palavra) this.mbr.dados;
    		binarioMbr = palavra.getPalavraCompleta();
    	}
   
		Object[][] dados = {
			{"pc" , this.pc },
			{"ir" , binarioIr },
			{"mbr" , binarioMbr },
			{"mar" , this.mar},
			{"ax" , this.ax},
			{"bx" , this.bx},
			{"cx" , this.cx},
			{"dx" , this.dx},
			{"x" , ula.getX()},
			{"y" , ula.getY()},
			{"flagE" , this.flagE},
			{"flagNe" , this.flagNe},
			{"flagG" , this.flagG},
			{"flagGe" , this.flagGe},
			{"flagL" , this.flagL},
			{"flagLe" , this.flagLe}
		};
		
		return dados;
    }
    
    public StringBuilder cicloDeBuscaParaMostrarNaTela(Firmware firmware, Memoria memoria) {
    	String[] sinais = firmware.getSinaisDeControle(0, this.ir);
    	StringBuilder builder = new StringBuilder();
 
    	for (int i = 0; i < sinais.length; i++) {
	    	for (int jota = 0; jota < sinais[i].length(); jota++) {
		    	if (jota == 1 && sinais[i].charAt(jota) == '1' && sinais[i].charAt(jota + 1) == '1') {
		    		builder.append("t1: mar <- pc  \n");
		    		builder.append("t1: x <- pc  \n");
		    		builder.append("t1: y <- 1  \n");
	    		}
		    	if (jota == 0 && sinais[i].charAt(jota) == '1' && sinais[i].charAt(19) == '1') {
		    		builder.append("t2: pc <- ula \n");
	    		} 
		    	if (jota == 22 && sinais[i].charAt(jota) == '1' && sinais[i].charAt(24) == '1') {
		    		builder.append("t2: memoria <- mar \n");
	    		} 
		    	if (jota == 23 && sinais[i].charAt(jota) == '1' && sinais[i].charAt(25) == '1') {
		    		builder.append("t3: mbr <- memoria \n");
	    		} 
		    	if (jota == 4 && sinais[i].charAt(jota) == '1' && sinais[i].charAt(15) == '1' && sinais[i].charAt(17) == '1') {
		    		builder.append("t4: ir <- mbr \n");
	    		}
			}
    	}
 
    	return builder;
    }

    public StringBuilder cicloDeExecucaoParaMostrarNaTela(Firmware firmware, int indice, Memoria memoria) {
//    	Barramento barramento = new Barramento();
//    	StringBuilder builder = new StringBuilder();
//    	Object caminhos[][] = new Object[27][27];
//    	int ind = 0;
//    	String[] sinaisDeControle = firmware.getSinaisDeControle(indice);
//    	for (int i = 0; i < sinaisDeControle.length; i++) {
//    		for (int j = 0; j < sinaisDeControle[i].length(); j++)
//	    		if (sinaisDeControle[i].charAt(j) == '1') {
//	        		caminhos[ind] = barramento.getCaminhos(j);
//	        		if ()
//	    		}
//	    	}
//    	}
    	int atual = 0;
    	boolean acabou = false;
    	StringBuilder builder = new StringBuilder();
		int end = memoria.getLinha();
    	while (!acabou) {
    		if (end + 1 != atual) {
		    	String[] sinaisDeControle = firmware.getSinaisDeControleParaMostrarNaTela(indice, firmware, memoria, atual);
		    	for (int i = 0; i < sinaisDeControle.length; i++) {
		    		switch (sinaisDeControle[i]) {
		    			case "00000000000000001010000000000":
				    		builder.append("t1: x <- irp1  \n");
				    		break;
		
		    			case "00000000000000100001000000001":
				    		builder.append("t2: y <- irp2  \n");
				    		break;
		
		    			case "00000100000000000000100000000":
				    		builder.append("t3 :ax <- ula  \n");
				    		break;
		
		    			case "00000001000000000000100000000":
				    		builder.append("t3: bx <- ula  \n");
				    		break;
		
		    			case "00000000010000000000100000000":
				    		builder.append("t3: cx <- ula  \n");
				    		break;
		
		    			case "00000000000100000000100000000":
				    		builder.append("t3: dx <- ula  \n");
				    		break;
		
		    			case "00000100000000100000000000000":
				    		builder.append("t1: ax <- irp2  \n");
				    		break;
		
		    			case "00000001000000100000000000000":
				    		builder.append("t1: bx <- irp2  \n");
				    		break;
		
		    			case "00000000010000100000000000000":
				    		builder.append("t1: cx <- irp2  \n");
				    		break;
		
		    			case "00000000000100100000000000000":
				    		builder.append("t1: dx <- irp2  \n");
				    		break;
		    		}	
		    	}
    		} else {
    			acabou = true;
    		}
    	}
    	return builder;
    }
    
    public Object[][] cicloDeExecucao(Firmware firmware, int indice, int atual) {
    	String[] sinaisDeControle = firmware.getSinaisDeControle(indice, this.ir);
		switch (sinaisDeControle[atual]) {
			case "00000000000000001010000000000":
				ula.setX(this.ir.getOperandoUm());
	    		break;

			case "00000000000000100001000000001":
				ula.setY(this.ir.getOperandoDois());
	    		break;

			case "00000100000000000000100000000":
	    		this.ax = Integer.toString(ula.getResultado("001"));
	    		break;

			case "00000001000000000000100000000":
	    		this.bx = Integer.toString(ula.getResultado("001"));
	    		break;

			case "00000000010000000000100000000":
	    		this.cx = Integer.toString(ula.getResultado("001"));
	    		break;

			case "00000000000100000000100000000":
	    		this.dx = Integer.toString(ula.getResultado("001"));
	    		break;

			case "00000100000000100000000000000":
				this.ax = this.ir.getOperandoDois();
	    		break;

			case "00000001000000100000000000000":
				this.bx = this.ir.getOperandoDois();
	    		break;

			case "00000000010000100000000000000":
				this.cx = this.ir.getOperandoDois();
	    		break;

			case "00000000000100100000000000000":
				this.dx = this.ir.getOperandoDois();
	    		break;
		}
		String binarioIr;
    	String binarioMbr;

    	if (this.ir == null) {
    		binarioIr = "0";
    	} else {
    		binarioIr = this.ir.getPalavraCompleta();
    	}
    	
    	if (this.mbr == null) {
    		binarioMbr = "0";
    	} else {
    		Palavra palavra = (Palavra) this.mbr.dados;
    		binarioMbr = palavra.getPalavraCompleta();
    	}
   
		Object[][] dados = {
			{"pc" , this.pc },
			{"ir" , binarioIr },
			{"mbr" , binarioMbr },
			{"mar" , this.mar},
			{"ax" , this.ax},
			{"bx" , this.bx},
			{"cx" , this.cx},
			{"dx" , this.dx},
			{"x" , ula.getX()},
			{"y" , ula.getY()},
			{"flagE" , this.flagE},
			{"flagNe" , this.flagNe},
			{"flagG" , this.flagG},
			{"flagGe" , this.flagGe},
			{"flagL" , this.flagL},
			{"flagLe" , this.flagLe}
		};
		
		return dados;
    }
}