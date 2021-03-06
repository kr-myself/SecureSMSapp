package project.csci6365.securesmsapp;

class Hash {
    private String hash;
    private String k[] = {"243F6A88", "85A308D3", "13198A2E", "03707344", "A4093822", "299F31D0", "082EFA98", "EC4E6C89"};
    private String z[] = {"243F6A88", "85A308D3", "13198A2E", "03707344", "A4093822", "299F31D0", "082EFA98", "EC4E6C89"};
	private int ROUND1[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
    private int ROUND2[] = {5, 14, 26, 18, 11, 28, 7, 16, 0, 23, 20, 22, 1, 10, 4, 8, 30, 3, 21, 9, 17, 24, 29, 6, 19, 12, 15, 13, 2, 25, 31, 27};
    private int ROUND3[] = {19, 9, 4, 20, 28, 17, 8, 22, 29, 14, 25, 12, 24, 30, 16, 26, 31, 15, 7, 3, 1, 0, 18, 27, 13, 6, 21, 10, 23, 11, 5, 2};
    
    private String INITIAL_CONST[] = {"243F6A88", "85A308D3", "13198A2E", "03707344", "A4093822", "299F31D0", "082EFA98", "EC4E6C89"};
    private String ROUND2_CONST[] = {"452821E6", "38D01377", "BE5466CF", "34E90C6C", "C0AC29B7", "C97C50DD", "3F84D5B5", "B5470917", "9216D5D9", "8979FB1B", "D1310BA6", "98DFB5AC", "2FFD72DB", "D01ADFB7", "B8E1AFED", "6A267E96", "BA7C9045", "F12C7F99", "24A19947", "B3916CF7", "0801F2E2", "858EFC16", "636920D8", "71574E69", "A458FEA3", "F4933D7E", "0D95748F", "728EB658", "718BCD58", "82154AEE", "7B54A41D", "C25A59B5"};
    private String ROUND3_CONST[] = {"9C30D539", "2AF26013", "C5D1B023", "286085F0", "CA417918", "B8DB38EF", "8E79DCB0", "603A180E", "6C9E0E8B", "B01E8A3E", "D71577C1", "BD314B27", "78AF2FDA", "55605C60", "E65525F3", "AA55AB94", "57489862", "63E81440", "55CA396A", "2AAB10B6", "B4CC5C34", "1141E8CE", "A15486AF", "7C72E993", "B3EE1411", "636FBC2A", "2BA9C55D", "741831F6", "CE5C3E16", "9B87931E", "AFD6BA33", "6C24CF5C"};
    
		
    Hash(String input) {
        if (input.length() < 128) {
            StringBuilder inputBuilder = new StringBuilder(input);
            while (inputBuilder.length() < 128)
                inputBuilder.append(" ");
            input = inputBuilder.toString();
            hash = hasher(input);
        } else if (input.length() > 128) {
            // Implement later
        } else {
            hash = hasher(input);
        }
    }

    private String hasher(String hash_string){
        int string_length = hash_string.length();
        int t = string_length / 4;

        //String Values Split Into 4 Then Hex'd
        String w[] = new String[t];
        for(int x= 0; x < t; x++){
            int start = x * 4;
            int end = start + 4;
            if(end < string_length){
                w[x] = hash_string.substring(start, end);
            }
            else{
                w[x] = hash_string.substring(start, string_length);
                int temp = 4 - w[x].length();
                for(int y = 0; y < temp; y++)
                    w[x] += " ";
            }
            w[x] = to_hex(w[x]);
        }

        //Round 1
        for(int x = 0; x < t; x++){
            int xx[] = new int[8];
            for(int y = 0; y < 8; y++){
                xx[y] = 7-y-x;
                while(xx[y] < 0){
                    xx[y] += 8;
                }
            }
            k[xx[0]] = FF(k[xx[0]], k[xx[1]], k[xx[2]], k[xx[3]], k[xx[4]], k[xx[5]], k[xx[6]], k[xx[7]], w[ROUND1[x]]);
        }

        //Round 2
        for(int x = 0; x < t; x++){
            int xx[] = new int[8];
            for(int y = 0; y < 8; y++){
                xx[y] = 7-y-x;
                while(xx[y] < 0){
                    xx[y] += 8;
                }
            }
            k[xx[0]] = GG(k[xx[0]], k[xx[1]], k[xx[2]], k[xx[3]], k[xx[4]], k[xx[5]], k[xx[6]], k[xx[7]], w[ROUND2[x]], ROUND2_CONST[x]);
        }

        //Round 3
        for(int x = 0; x < t; x++){
            int xx[] = new int[8];
            for(int y = 0; y < 8; y++){
                xx[y] = 7-y-x;
                while(xx[y] < 0){
                    xx[y] += 8;
                }
            }
            k[xx[0]] = HH(k[xx[0]], k[xx[1]], k[xx[2]], k[xx[3]], k[xx[4]], k[xx[5]], k[xx[6]], k[xx[7]], w[ROUND3[x]], ROUND3_CONST[x]);
        }

        //Finishing Up
        for(int x = 0; x < 8; x ++){
            Long temp_var = Long.parseLong(z[x], 16);
            temp_var += Long.parseLong(k[x], 16);
            z[x] = Long.toHexString(temp_var);
        }

        String final_strings[] = {"000000FF", "FF000000", "00FF0000", "0000FF00"};
        for(int x = 0; x < 4; x++){
            int xx[] = new int[4];
            for(int y = 0; y < 4; y++){
                xx[y] = 3-y-x;
                while(xx[y] < 0){
                    xx[y] += 4;
                }
            }

            String temp1 = and_result(z[7], final_strings[xx[0]]);
            String temp2 = and_result(z[6], final_strings[xx[1]]);
            String temp3 = and_result(z[5], final_strings[xx[2]]);
            String temp4 = and_result(z[4], final_strings[xx[3]]);
            String r = xor_result(temp1, temp2);
            r = xor_result(r, temp3);
            r = xor_result(r, temp4);

            String temp_mod = special_shift(Long.toBinaryString(Long.parseLong(r, 16)), (x+1)*8);
            z[x] = Long.toHexString(Long.parseLong(r, 16) + Long.parseLong(temp_mod, 2));
            while(z[x].length() > 8){
                z[x] = z[x].substring(1);
            }
        }

        String r = z[0];
        r += z[1];
        r += z[2];
        r += z[3];
        return r;
    }

    String getHash() {
        return hash;
    }

    private String to_hex(String m){
        char chars[] = m.toCharArray();
        StringBuilder r = new StringBuilder();
        for(char c: chars){
            r.append(Integer.toHexString((int)c));
        }
        return r.toString();
    }

    private String to_binary(String m){
        StringBuilder r = new StringBuilder();
        while(m.length() > 8){
            m = m.substring(1);
        }
        for(int x = 0; x < m.length(); x += 2){
            String hex_chr = m.substring(x, x+2);
            int hex_value = (Integer.parseInt(hex_chr, 16));
            if (Integer.toBinaryString(hex_value).length() < 8){
                for (int y = 0; y < 8 - Integer.toBinaryString(hex_value).length(); y ++){
                    r.append("0");
                }
            }
            r.append(Integer.toBinaryString(hex_value));
        }
        return r.toString();
    }

    private String binary_to_hex(String m){
        StringBuilder r = new StringBuilder();
        for(int x = 0; x < m.length(); x += 4){
            String temp_string = m.substring(x, x+4);
            int temp = Integer.parseInt(temp_string, 2);
            r.append(Integer.toString(temp, 16));
        }
        return r.toString();
    }

    private String xor_result(String a, String b){
        StringBuilder r = new StringBuilder();
        a = to_binary(a);
        b = to_binary(b);
        for(int x = 0; x < a.length(); x++){
            if(a.substring(x, x+1).equals(b.substring(x, x+1)))
                r.append("0");
            else
                r.append("1");
        }
        return binary_to_hex(r.toString());
    }

    private String and_result(String a, String b){
        StringBuilder r = new StringBuilder();
        a = to_binary(a);
        b = to_binary(b);
        for(int x = 0; x < a.length(); x++){
            if(a.substring(x, x+1).equals(b.substring(x, x+1)) && a.substring(x, x+1).equals("1"))
                r.append("1");
            else
                r.append("0");
        }
        return binary_to_hex(r.toString());
    }

    private String F(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        String temp1 = and_result(A1, A4);
        String temp2 = and_result(A2, A5);
        String temp3 = and_result(A3, A6);
        String temp4 = and_result(A0, A1);
        String r = xor_result(temp1, temp2);
        r = xor_result(r, temp3);
        r = xor_result(r, temp4);
        r = xor_result(r, A0);
        return r;
    }

    private String G(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        String temp1 = and_result(A1, A2);
        temp1 = and_result(temp1, A3);
        String temp2 = and_result(A2, A4);
        temp2 = and_result(temp2, A5);
        String temp3 = and_result(A1, A2);
        String temp4 = and_result(A1, A4);
        String temp5 = and_result(A2, A6);
        String temp6 = and_result(A3, A5);
        String temp7 = and_result(A4, A5);
        String temp8 = and_result(A0, A2);
        String r = xor_result(temp1, temp2);
        r = xor_result(r, temp3);
        r = xor_result(r, temp4);
        r = xor_result(r, temp5);
        r = xor_result(r, temp6);
        r = xor_result(r, temp7);
        r = xor_result(r, temp8);
        r = xor_result(r, A0);
        return r;
    }

    private String H(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        String temp1 = and_result(A1, A2);
        temp1 = and_result(temp1, A3);
        String temp2 = and_result(A1, A4);
        String temp3 = and_result(A2, A5);
        String temp4 = and_result(A3, A6);
        String temp5 = and_result(A0, A3);
        String r = xor_result(temp1, temp2);
        r = xor_result(r, temp3);
        r = xor_result(r, temp4);
        r = xor_result(r, temp5);
        r = xor_result(r, A0);
        return r;
    }

    private String F_phi(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        return F(A1, A0, A3, A5, A6, A2, A4);
    }

    private String G_phi(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        return G(A4, A2, A1, A0, A5, A3, A6);
    }

    private String H_phi(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        return H(A6, A1, A2, A3, A4, A5, A0);
    }

    private String special_shift(String a, int length){
        //Regular Shift Deletes Bits That Over Reach, Paper Shift Moves Them To Other Side Therefore Can't Use >>>
        while(a.length() != 32){
            a = "0" + a;
        }
        String r = a.substring(a.length() - length);
        r += a.substring(0, a.length() - length);
        return r;
    }

    private String FF(String A7, String A6, String A5, String A4, String A3, String A2, String A1, String A0, String w){
        String r;
        String temp = F_phi(A6, A5, A4, A3, A2, A1, A0);
        String temp7 = special_shift(Long.toBinaryString(Long.parseLong(temp, 16)), 7);
        String temp11 = special_shift(Long.toBinaryString(Long.parseLong(A7, 16)), 11);

        long hh = Long.parseLong(temp7, 2) + Long.parseLong(temp11, 2) + Long.parseLong(w, 16);
        r = Long.toHexString(hh);
        if(r.length() > 8){
            r = r.substring(r.length() - 8);
        }

        return r;
    }

    private String GG(String A7, String A6, String A5, String A4, String A3, String A2, String A1, String A0, String w, String c){
        String r;
        String temp = G_phi(A6, A5, A4, A3, A2, A1, A0);
        String temp7 = special_shift(Long.toBinaryString(Long.parseLong(temp, 16)), 7);
        String temp11 = special_shift(Long.toBinaryString(Long.parseLong(A7, 16)), 11);

        long hh = Long.parseLong(temp7, 2) + Long.parseLong(temp11, 2) + Long.parseLong(w, 16)  + Long.parseLong(c, 16);
        r = Long.toHexString(hh);
        if(r.length() > 8){
            r = r.substring(r.length() - 8);
        }

        return r;
    }

    private String HH(String A7, String A6, String A5, String A4, String A3, String A2, String A1, String A0, String w, String c){
        String r;
        String temp = H_phi(A6, A5, A4, A3, A2, A1, A0);
        String temp7 = special_shift(Long.toBinaryString(Long.parseLong(temp, 16)), 7);
        String temp11 = special_shift(Long.toBinaryString(Long.parseLong(A7, 16)), 11);

        long hh = Long.parseLong(temp7, 2) + Long.parseLong(temp11, 2) + Long.parseLong(w, 16)  + Long.parseLong(c, 16);
        r = Long.toHexString(hh);
        if(r.length() > 8){
            r = r.substring(r.length() - 8);
        }

        return r;
    }

}

/* The original Hash function that Oscar made
public class Hash {
    public static void split(String hash_string){
        System.out.println(hash_string);
    }
    
    public static int fits(int a, int b){
        int value = 0;
        for(; a > 0; a -= b)
            value++;

        return value;
    }
    
    public static String to_hex(String m){
        char chars[] = m.toCharArray();
        StringBuilder r = new StringBuilder();
        for(char c: chars){
            r.append(Integer.toHexString((int)c));
        }
        return r.toString();
    }
    
    public static String to_string(String m){
        StringBuilder r = new StringBuilder("");
        for(int x = 0; x < m.length(); x += 2){
            String temp = m.substring(x, x+2);
            r.append((char) Integer.parseInt(temp, 16));
        }
        return r.toString();
    }
    
    public static String to_binary(String m){
        StringBuilder r = new StringBuilder("");
        while(m.length() > 8){
            m = m.substring(1);
        }
        for(int x = 0; x < m.length(); x += 2){
            String hex_chr = m.substring(x, x+2);
            int hex_value = (Integer.parseInt(hex_chr, 16));
            if (Integer.toBinaryString(hex_value).length() < 8){
             for (int y = 0; y < 8 - Integer.toBinaryString(hex_value).length(); y ++){
                 r.append("0");
             }   
            }
            r.append(Integer.toBinaryString(hex_value));
        }
        return r.toString();
    }
    
    public static String binary_to_hex(String m){
        StringBuilder r = new StringBuilder("");
         for(int x = 0; x < m.length(); x += 4){
            String temp_string = m.substring(x, x+4);
            int temp = Integer.parseInt(temp_string, 2);
            r.append(Integer.toString(temp, 16));
        }
        return r.toString();
    }
    
    public static String xor_result(String a, String b){
        StringBuilder r = new StringBuilder("");
        a = to_binary(a);
        b = to_binary(b);
        for(int x = 0; x < a.length(); x++){
            if(a.substring(x, x+1).equals(b.substring(x, x+1)))
                r.append("0");
            else
                r.append("1");
        }
        return binary_to_hex(r.toString());
    }
    
    public static String and_result(String a, String b){
        StringBuilder r = new StringBuilder("");
        a = to_binary(a);
        b = to_binary(b);
        for(int x = 0; x < a.length(); x++){
            if(a.substring(x, x+1).equals(b.substring(x, x+1)) && a.substring(x, x+1).equals("1"))
                r.append("1");
            else
                r.append("0");
        }
        return binary_to_hex(r.toString());
    }
    
    public static String hasher(String hash_string){
        //Initiaal k Values, Same As Paper
        String k[] = {"243F6A88", "85A308D3", "13198A2E", "03707344", "A4093822", "299F31D0", "082EFA98", "EC4E6C89"};
        String z[] = {"243F6A88", "85A308D3", "13198A2E", "03707344", "A4093822", "299F31D0", "082EFA98", "EC4E6C89"};
        //Add Padding If Length Too Short
        while(hash_string.length() != 128){
            hash_string += " ";
        }
        int string_length = hash_string.length();
        int t = fits(string_length, 4);
        
        //String Values Split Into 4 Then Hex'd
        String w[] = new String[t];
        for(int x= 0; x < t; x++){
            int start = x * 4;
            int end = start + 4;
            if(end < string_length){
                w[x] = hash_string.substring(start, end);
            }
            else{
                w[x] = hash_string.substring(start, string_length);
                int temp = 4 - w[x].length();
                for(int y = 0; y < temp; y++)
                    w[x] += " ";
            }
            w[x] = to_hex(w[x]);
        }
        
        //Round 1
        for(int x = 0; x < t; x++){
            int xx[] = new int[8];
            for(int y = 0; y < 8; y++){
                xx[y] = 7-y-x;
                while(xx[y] < 0){
                    xx[y] += 8;
                }
            }
            k[xx[0]] = FF(k[xx[0]], k[xx[1]], k[xx[2]], k[xx[3]], k[xx[4]], k[xx[5]], k[xx[6]], k[xx[7]], w[x]);
        }
        
        //Round 2
        for(int x = 0; x < t; x++){
            int xx[] = new int[8];
            for(int y = 0; y < 8; y++){
                xx[y] = 7-y-x;
                while(xx[y] < 0){
                    xx[y] += 8;
                }
            }
            k[xx[0]] = GG(k[xx[0]], k[xx[1]], k[xx[2]], k[xx[3]], k[xx[4]], k[xx[5]], k[xx[6]], k[xx[7]], w[x], "AAAAAAA");
        }
        
        //Round 3
        for(int x = 0; x < t; x++){
            int xx[] = new int[8];
            for(int y = 0; y < 8; y++){
                xx[y] = 7-y-x;
                while(xx[y] < 0){
                    xx[y] += 8;
                }
            }
            k[xx[0]] = HH(k[xx[0]], k[xx[1]], k[xx[2]], k[xx[3]], k[xx[4]], k[xx[5]], k[xx[6]], k[xx[7]], w[x], "AAAAAAA");
        }
        
        //Finishing Up
        for(int x = 0; x < 8; x ++){
            Long temp_var = Long.parseLong(z[x], 16);
            temp_var += Long.parseLong(k[x], 16);
            z[x] = Long.toHexString(temp_var);
        }
        
        String final_strings[] = {"000000FF", "FF000000", "00FF0000", "0000FF00"};
        for(int x = 0; x < 4; x++){
            int xx[] = new int[4];
            for(int y = 0; y < 4; y++){
                xx[y] = 3-y-x;
                while(xx[y] < 0){
                    xx[y] += 4;
                }
            }

            String temp1 = and_result(z[7], final_strings[xx[0]]);
            String temp2 = and_result(z[6], final_strings[xx[1]]);
            String temp3 = and_result(z[5], final_strings[xx[2]]);
            String temp4 = and_result(z[4], final_strings[xx[3]]);
            String r = xor_result(temp1, temp2);
            r = xor_result(r, temp3);
            r = xor_result(r, temp4);
            
            String temp_mod = special_shift(Long.toBinaryString(Long.parseLong(r, 16)), (x+1)*8);
            z[x] = Long.toHexString(Long.parseLong(r, 16) + Long.parseLong(temp_mod, 2));
            while(z[x].length() > 8){
                z[x] = z[x].substring(1);
            }
        }
        
        String r = z[0];
        r += z[1];
        r += z[2];
        r += z[3];
        return r;
    }
    
    public static String F(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        String temp1 = and_result(A1, A4);
        String temp2 = and_result(A2, A5);
        String temp3 = and_result(A3, A6);
        String temp4 = and_result(A0, A1);
        String r = xor_result(temp1, temp2);
        r = xor_result(r, temp3);
        r = xor_result(r, temp4);
        r = xor_result(r, A0);
        return r;
    }
    
    public static String G(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        String temp1 = and_result(A1, A2);
        temp1 = and_result(temp1, A3);
        String temp2 = and_result(A2, A4);
        temp2 = and_result(temp2, A5);
        String temp3 = and_result(A1, A2);
        String temp4 = and_result(A1, A4);
        String temp5 = and_result(A2, A6);
        String temp6 = and_result(A3, A5);
        String temp7 = and_result(A4, A5);
        String temp8 = and_result(A0, A2);
        String r = xor_result(temp1, temp2);
        r = xor_result(r, temp3);
        r = xor_result(r, temp4);
        r = xor_result(r, temp5);
        r = xor_result(r, temp6);
        r = xor_result(r, temp7);
        r = xor_result(r, temp8);
        r = xor_result(r, A0);
        return r;
    }
    
    public static String H(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        String temp1 = and_result(A1, A2);
        temp1 = and_result(temp1, A3);
        String temp2 = and_result(A1, A4);
        String temp3 = and_result(A2, A5);
        String temp4 = and_result(A3, A6);
        String temp5 = and_result(A0, A3);
        String r = xor_result(temp1, temp2);
        r = xor_result(r, temp3);
        r = xor_result(r, temp4);
        r = xor_result(r, temp5);
        r = xor_result(r, A0);
        return r;
    }
    
    public static String F_phi(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        return F(A1, A0, A3, A5, A6, A2, A4);
    }
    
    public static String G_phi(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        return F(A4, A2, A1, A0, A5, A3, A6);
    }
    
    public static String H_phi(String A6, String A5, String A4, String A3, String A2, String A1, String A0){
        return F(A6, A1, A2, A3, A4, A5, A0);
    }
    
    public static String special_shift(String a, int length){
        //Regular Shift Deletes Bits That Over Reach, Paper Shift Moves Them To Other Side Therefore Can't Use >>>
        while(a.length() != 32){
            a = "0" + a;
        }
        String r = a.substring(a.length() - length);
        r += a.substring(0, a.length() - length);
        return r;
    }
    
    public static String FF(String A7, String A6, String A5, String A4, String A3, String A2, String A1, String A0, String w){
        String r = "";
        String temp = F_phi(A6, A5, A4, A3, A2, A1, A0);
        String temp7 = special_shift(Long.toBinaryString(Long.parseLong(temp, 16)), 7);
        String temp11 = special_shift(Long.toBinaryString(Long.parseLong(A7, 16)), 11);
        
        long hh = Long.parseLong(temp7, 2) + Long.parseLong(temp11, 2) + Long.parseLong(w, 16);
        r = Long.toHexString(hh);
        if(r.length() > 8){
            r = r.substring(r.length() - 8);
        }

        return r;
    }

    public static String GG(String A7, String A6, String A5, String A4, String A3, String A2, String A1, String A0, String w, String c){
        String r = "";
        String temp = G_phi(A6, A5, A4, A3, A2, A1, A0);
        String temp7 = special_shift(Long.toBinaryString(Long.parseLong(temp, 16)), 7);
        String temp11 = special_shift(Long.toBinaryString(Long.parseLong(A7, 16)), 11);
        
        long hh = Long.parseLong(temp7, 2) + Long.parseLong(temp11, 2) + Long.parseLong(w, 16)  + Long.parseLong(c, 16);
        r = Long.toHexString(hh);
        if(r.length() > 8){
            r = r.substring(r.length() - 8);
        }
        
        return r;
    }
    
    public static String HH(String A7, String A6, String A5, String A4, String A3, String A2, String A1, String A0, String w, String c){
        String r = "";
        String temp = H_phi(A6, A5, A4, A3, A2, A1, A0);
        String temp7 = special_shift(Long.toBinaryString(Long.parseLong(temp, 16)), 7);
        String temp11 = special_shift(Long.toBinaryString(Long.parseLong(A7, 16)), 11);
        
        long hh = Long.parseLong(temp7, 2) + Long.parseLong(temp11, 2) + Long.parseLong(w, 16)  + Long.parseLong(c, 16);
        r = Long.toHexString(hh);
        if(r.length() > 8){
            r = r.substring(r.length() - 8);
        }
        
        return r;
    }
    
    public static void main(String[] args) {
        String message = "This is only message for testing how HAVAL algorithm work and show how the result were getting after hashing, this were made iam";
        System.out.println(hasher(message));

 
    }
    
}
*/