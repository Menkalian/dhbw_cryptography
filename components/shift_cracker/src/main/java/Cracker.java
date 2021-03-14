import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Cracker {
    // static instance
    private static Cracker instance = new Cracker();
    // port
    public Port port;

    private Cracker() {
        port = new Port();
    }

    public static Cracker getInstance() {
        return instance;
    }

    private String innerDecrypt(String encrypted) {
        int recordKey = -1;
        double recordScore = Double.MIN_VALUE;

        byte[] unicode = encrypted.getBytes(StandardCharsets.UTF_8);

        for (int shift = 0 ; shift <= 25 ; shift++) {
            double score = calcScore(shift, unicode);
            if (score >= recordScore) {
                recordScore = score;
                recordKey = shift;
            }
        }

        return new String(shiftString(unicode, recordKey));
    }

    private byte[] shiftString(byte[] data, int shiftKey) {
        int[] dataCopy = new int[data.length];
        for (int i = 0 ; i < data.length ; i++) {
            dataCopy[i] = data[i];
        }

        for (int x = 0 ; x <= data.length - 1 ; x++) {
            dataCopy[x] = data[x];

            if (data[x] >= 'A' && data[x] <= 'Z') {
                dataCopy[x] += shiftKey;
                if (dataCopy[x] > 'Z') {
                    dataCopy[x] -= 26;
                }
                if (dataCopy[x] < 'A') {
                    dataCopy[x] += 26;
                }
            } else if (data[x] >= 'a' && data[x] <= 'z') {
                dataCopy[x] += shiftKey;
                if (dataCopy[x] > 'z') {
                    dataCopy[x] -= 26;
                }
                if (dataCopy[x] < 'a') {
                    dataCopy[x] += 26;
                }
            }
        }

        byte[] toReturn = new byte[dataCopy.length];
        for (int i = 0 ; i < toReturn.length ; i++) {
            toReturn[i] = (byte) dataCopy[i];
        }

        return toReturn;
    }

    private double calcScore(int shift, byte[] unicode) {
        byte[] unicodeCopy = shiftString(unicode, shift);
        char[] transformed = new char[unicode.length];

        for (int count = 0 ; count < transformed.length ; count++) {
            transformed[count] = (char) unicodeCopy[count];
        }

        // Weight source: https://www.xarg.org/2010/05/cracking-a-caesar-cipher/
        double[] weights = new double[] {
                6.51, 1.89, 3.06, 5.08, 17.4,
                1.66, 3.01, 4.76, 7.55, 0.27,
                1.21, 3.44, 2.53, 9.78, 2.51,
                0.29, 0.02, 7.00, 7.27, 6.15,
                4.35, 0.67, 1.89, 0.03, 0.04, 1.13
        };

        double[] occurences = new double[26];
        Arrays.fill(occurences, 0.0);

        for (char process : transformed) {
            int index = Character.toLowerCase(process) - 'a';
            if (index >= 0 && index < 26)
                occurences[index] += 1.0;
        }

        // Combine occurences and weights to find score
        double score = 0;
        for (int i = 0 ; i < occurences.length ; i++) {
            score += occurences[i] * weights[i];
        }
        return score;
    }

    public class Port implements ICracker {
        @Override
        public String decrypt(String encryptedMessage) {
            return innerDecrypt(encryptedMessage);
        }
    }
}
