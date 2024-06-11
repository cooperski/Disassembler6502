import java.io.FileInputStream;
import java.io.IOException;

public class Disassembler6502 {

    static Object[][] Opcodes = {
            /* Name, Imm,  ZP,   ZPX,  ZPY,  ABS, ABSX, ABSY,  IND, INDX, INDY, IMPL, REL */
            {"ADC", 0x69, 0x65, 0x75, null, 0x6d, 0x7d, 0x79, null, 0x61, 0x71, null, null},
            {"AND", 0x29, 0x25, 0x35, null, 0x2d, 0x3d, 0x39, null, 0x21, 0x31, null, null},
            {"ASL", null, 0x06, 0x16, null, 0x0e, 0x1e, null, null, null, null, 0x0a, null},
            {"BIT", null, 0x24, null, null, 0x2c, null, null, null, null, null, null, null},
            {"BPL", null, null, null, null, null, null, null, null, null, null, null, 0x10},
            {"BMI", null, null, null, null, null, null, null, null, null, null, null, 0x30},
            {"BVC", null, null, null, null, null, null, null, null, null, null, null, 0x50},
            {"BVS", null, null, null, null, null, null, null, null, null, null, null, 0x70},
            {"BCC", null, null, null, null, null, null, null, null, null, null, null, 0x90},
            {"BCS", null, null, null, null, null, null, null, null, null, null, null, 0xb0},
            {"BNE", null, null, null, null, null, null, null, null, null, null, null, 0xd0},
            {"BEQ", null, null, null, null, null, null, null, null, null, null, null, 0xf0},
            {"BRK", null, null, null, null, null, null, null, null, null, null, 0x00, null},
            {"CMP", 0xc9, 0xc5, 0xd5, null, 0xcd, 0xdd, 0xd9, null, 0xc1, 0xd1, null, null},
            {"CPX", 0xe0, 0xe4, null, null, 0xec, null, null, null, null, null, null, null},
            {"CPY", 0xc0, 0xc4, null, null, 0xcc, null, null, null, null, null, null, null},
            {"DEC", null, 0xc6, 0xd6, null, 0xce, 0xde, null, null, null, null, null, null},
            {"EOR", 0x49, 0x45, 0x55, null, 0x4d, 0x5d, 0x59, null, 0x41, 0x51, null, null},
            {"CLC", null, null, null, null, null, null, null, null, null, null, 0x18, null},
            {"SEC", null, null, null, null, null, null, null, null, null, null, 0x38, null},
            {"CLI", null, null, null, null, null, null, null, null, null, null, 0x58, null},
            {"SEI", null, null, null, null, null, null, null, null, null, null, 0x78, null},
            {"CLV", null, null, null, null, null, null, null, null, null, null, 0xb8, null},
            {"CLD", null, null, null, null, null, null, null, null, null, null, 0xd8, null},
            {"SED", null, null, null, null, null, null, null, null, null, null, 0xf8, null},
            {"INC", null, 0xe6, 0xf6, null, 0xee, 0xfe, null, null, null, null, null, null},
            {"JMP", null, null, null, null, 0x4c, null, null, 0x6c, null, null, null, null},
            {"JSR", null, null, null, null, 0x20, null, null, null, null, null, null, null},
            {"LDA", 0xa9, 0xa5, 0xb5, null, 0xad, 0xbd, 0xb9, null, 0xa1, 0xb1, null, null},
            {"LDX", 0xa2, 0xa6, null, 0xb6, 0xae, null, 0xbe, null, null, null, null, null},
            {"LDY", 0xa0, 0xa4, 0xb4, null, 0xac, 0xbc, null, null, null, null, null, null},
            {"LSR", null, 0x46, 0x56, null, 0x4e, 0x5e, null, null, null, null, 0x4a, null},
            {"NOP", null, null, null, null, null, null, null, null, null, null, 0xea, null},
            {"ORA", 0x09, 0x05, 0x15, null, 0x0d, 0x1d, 0x19, null, 0x01, 0x11, null, null},
            {"TAX", null, null, null, null, null, null, null, null, null, null, 0xaa, null},
            {"TXA", null, null, null, null, null, null, null, null, null, null, 0x8a, null},
            {"DEX", null, null, null, null, null, null, null, null, null, null, 0xca, null},
            {"INX", null, null, null, null, null, null, null, null, null, null, 0xe8, null},
            {"TAY", null, null, null, null, null, null, null, null, null, null, 0xa8, null},
            {"TYA", null, null, null, null, null, null, null, null, null, null, 0x98, null},
            {"DEY", null, null, null, null, null, null, null, null, null, null, 0x88, null},
            {"INY", null, null, null, null, null, null, null, null, null, null, 0xc8, null},
            {"ROR", null, 0x66, 0x76, null, 0x6e, 0x7e, null, null, null, null, 0x6a, null},
            {"ROL", null, 0x26, 0x36, null, 0x2e, 0x3e, null, null, null, null, 0x2a, null},
            {"RTI", null, null, null, null, null, null, null, null, null, null, 0x40, null},
            {"RTS", null, null, null, null, null, null, null, null, null, null, 0x60, null},
            {"SBC", 0xe9, 0xe5, 0xf5, null, 0xed, 0xfd, 0xf9, null, 0xe1, 0xf1, null, null},
            {"STA", null, 0x85, 0x95, null, 0x8d, 0x9d, 0x99, null, 0x81, 0x91, null, null},
            {"TXS", null, null, null, null, null, null, null, null, null, null, 0x9a, null},
            {"TSX", null, null, null, null, null, null, null, null, null, null, 0xba, null},
            {"PHA", null, null, null, null, null, null, null, null, null, null, 0x48, null},
            {"PLA", null, null, null, null, null, null, null, null, null, null, 0x68, null},
            {"PHP", null, null, null, null, null, null, null, null, null, null, 0x08, null},
            {"PLP", null, null, null, null, null, null, null, null, null, null, 0x28, null},
            {"STX", null, 0x86, null, 0x96, 0x8e, null, null, null, null, null, null, null},
            {"STY", null, 0x84, 0x94, null, 0x8c, null, null, null, null, null, null, null},
            {"???", null, null, null, null, null, null, null, null, null, null, null, null}
    };

    private static void hexDump(byte[] bytes) {
        // we and with 0xff to mask off the bits when sign-extended.
        int start = (bytes[1] & 0xff) * 256 + (bytes[0] & 0xff);
        System.out.println(start);
        int pc = start;
        StringBuilder chars = new StringBuilder();

        for (int x = 2; x < bytes.length; x++) {
            if ((pc - start) % 8 == 0) {
                System.out.printf(" %s", chars);
                System.out.printf("%n%04X:", pc);
                chars.setLength(0);
            }
            char c = (char) (bytes[x] & 0xff);
            if (c >= 32 && c <= 127)
                chars.append(c);
            else
                chars.append('.');
            System.out.printf(" %02X", bytes[x]);
            pc++;
        }
    }

    private static void disassemble(byte[] bytes) {
        int pc = (bytes[1] & 0xff) * 256 + (bytes[0] & 0xff);
        int b, o, result, byte1, byte2, offset;
        String mnemonic;

        System.out.println();

        // iterate through the bytes[] array
        for (int i = 2; i < bytes.length; i++) {
            b = (char) (bytes[i] & 0xff);
            boolean matchFound = false;

            // check against the Opcodes 2D array only for non-string and non-null elements
            for (int r = 0; r < Opcodes.length; r++) {
                for (int c = 1; c < Opcodes[r].length; c++) {
                    // avoid comparing against null objects
                    if (Opcodes[r][c] != null) {
                        o = (int) (Opcodes[r][c]);
                        if (b == o) {
                            // match found; proceed to if statement OR switch statement
                            matchFound = true;
                            mnemonic = Opcodes[r][0].toString();

                            // if we have a match AND the remaining bytes are <= 2, this is junk data,
                            // and we need to break from this iteration
                            if (bytes.length - i <= 2) {
                                mnemonic = "???";
                                System.out.printf("%n%04X:   %02X %11s", pc, b, mnemonic);
                                pc++;
                                break;
                            }

                            // switch statement based upon column number of match
                            result = switch(c) {
                                // Immediate - 2 bytes
                                case 1 -> {
                                    // for each case, ensure we have enough bytes left to read
                                    byte1 = bytes[i + 1] & 0xff;
                                    System.out.printf("%n%04X:   %02X %02X %8s #$%02X", pc, b, byte1, mnemonic, byte1);
                                    yield 2;
                                }
                                // Zero Page - 2 bytes
                                case 2 -> {
                                    byte1 = bytes[i+1] & 0xff;
                                    System.out.printf("%n%04X:   %02X %02X %8s $%02X", pc, b, byte1, mnemonic, byte1);
                                    yield 2;
                                }
                                // Zero Page,X - 2 bytes
                                case 3 -> {
                                    byte1 = bytes[i+1] & 0xff;
                                    System.out.printf("%n%04X:   %02X %02X %8s $%02X,X", pc, b, byte1, mnemonic, byte1);
                                    yield 2;
                                }
                                // Zero Page,Y - 2 bytes
                                case 4 -> {
                                    byte1 = bytes[i+1] & 0xff;
                                    System.out.printf("%n%04X:   %02X %02X %7s $%02X,Y", pc, b, byte1, mnemonic, byte1);
                                    yield 2;
                                }
                                // case 5: Absolute - 3 bytes
                                // case 8: Indirect - 3 bytes
                                case 5, 8 -> {
                                    byte1 = bytes[i + 2] & 0xff;
                                    byte2 = bytes[i + 1] & 0xff;
                                    System.out.printf("%n%04X:   %02X %02X %02X %5s $%02X%02X", pc, b, byte2, byte1, mnemonic, byte1, byte2);
                                    yield 3;
                                }
                                // Absolute,X - 3 bytes
                                case 6 -> {
                                    byte1 = bytes[i + 2] & 0xff;
                                    byte2 = bytes[i + 1] & 0xff;
                                    System.out.printf("%n%04X:   %02X %02X %02X %5s $%02X%02X,X", pc, b, byte2, byte1, mnemonic, byte1, byte2);
                                    yield 3;
                                }
                                // Absolute, Y - 3 bytes
                                case 7 -> {
                                    byte1 = bytes[i+2] & 0xff;
                                    byte2 = bytes[i+1] & 0xff;
                                    System.out.printf("%n%04X:   %02X %02X %02X %5s $%02X%02X,Y", pc, b, byte2, byte1, mnemonic, byte1, byte2);
                                    yield 3;
                                }
                                // Indirect,X - 2 bytes
                                case 9 -> {
                                    byte1 = bytes[i+1] & 0xff;
                                    System.out.printf("%n%04X:   %02X %02X %8s ($%02X,X)", pc, b, byte1, mnemonic, byte1);
                                    yield 2;
                                }
                                // Indirect,Y - 2 bytes
                                case 10 -> {
                                    byte1 = bytes[i+1] & 0xff;
                                    System.out.printf("%n%04X:   %02X %02X %8s ($%02X),Y", pc, b, byte1, mnemonic, byte1);
                                    yield 2;
                                }
                                // Implied - 1 byte
                                case 11 -> {
                                    System.out.printf("%n%04X:   %02X %11s", pc, b, mnemonic);
                                    yield 1;
                                }
                                // Relative - 2 bytes
                                case 12 -> {
                                    byte1 = bytes[i+1] & 0xff;
                                    int byte1x = bytes[i+1];
                                    offset = pc + byte1x + 2;
                                    System.out.printf("%n%04X:   %02X %02X %8s $%04X", pc, b, byte1, mnemonic, offset);
                                    yield 2;
                                }
                                default -> {
                                    // NOTE: no cases match here as of now.
                                    System.out.println("\n***DEFAULT***");
                                    yield 1;
                                }
                            };
                            // increment pc and i based on result of switch statement
                            pc += result;
                            i += result - 1;
                        }
                    }
                }
            }
            // Match not found among Opcodes 2D array elements; print pc, hex value, and '???' mnemonic
            if (!matchFound) {
                mnemonic = "???";
                System.out.printf("%n%04X:   %02X %11s", pc, b, mnemonic);
                pc++;
            }
        }
    } // end disassemble

    public static void main(String[] args) throws IOException {
        FileInputStream in = new FileInputStream("congrats.prg");
        byte[] file = in.readAllBytes();
        in.close();

        hexDump(file);
        disassemble(file);
    }
}
