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

@CContext(Headers.class)
@CLibrary("triple")
public class TripletLib {
    /*
    @CEnum("type_t")
    enum DataType {
        I,
        F,
        S;

        @CEnumValue
        public native int getCValue();

        @CEnumLookup
        public static native DataType fromCValue(int value);
    }
    */
    @CFunction(transition = Transition.NO_TRANSITION)
    public static native OOTriple allocRandomTriple();

    @CFunction(transition = Transition.NO_TRANSITION)
    public static native void freeTriple(OOTriple triple);

    public static long getThing(OOTriple triple) {
	return triple.subject().getId();
    }
}
