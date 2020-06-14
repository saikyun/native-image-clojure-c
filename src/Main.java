import java.io.*;
import org.graalvm.polyglot.*;
import org.graalvm.nativeimage.*;
import java.util.*;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.constant.CConstant;
import org.graalvm.nativeimage.c.constant.CEnum;
import org.graalvm.nativeimage.c.constant.CEnumLookup;
import org.graalvm.nativeimage.c.constant.CEnumValue;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.CurrentIsolate;
import org.graalvm.nativeimage.c.function.CEntryPointLiteral;
import org.graalvm.nativeimage.c.function.CLibrary;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.function.CFunction.Transition;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer;
import org.graalvm.nativeimage.c.struct.AllowWideningCast;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CFieldAddress;
import org.graalvm.nativeimage.c.struct.CFieldOffset;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.nativeimage.c.struct.SizeOf;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.graalvm.word.PointerBase;
import org.graalvm.word.SignedWord;
import org.graalvm.word.UnsignedWord;
import org.graalvm.word.WordFactory;
import wat.cool.OOTriple;

public class Main {
    public static void main(String[] args) throws IOException {
        long iterations = 100_000_000L;
        long start = System.currentTimeMillis();
        long sum = 0;

        for (long i = 0; i < iterations; i++) {
            OOTriple triple = TripletLib.allocRandomTriple();
            sum += triple.subject().getId(); // + triple.predicate().getId() + triple.object().getId();
            TripletLib.freeTriple(triple);
        }

        long end = System.currentTimeMillis();
        double timeTaken = ((double) (end - start) * 1_000_000L)/iterations;

        System.out.println("Final sum is: " + sum + ", time taken per iteration in nano seconds: " + timeTaken);
    }
}
