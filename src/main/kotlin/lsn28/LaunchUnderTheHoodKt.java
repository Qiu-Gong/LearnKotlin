package lsn28;

import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.ContinuationKt;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DelayKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
public final class LaunchUnderTheHoodKt {
    // 1
    public static final void main() {
        testStartCoroutine();
        Thread.sleep(2000L);
    }

    // 2
    private static final Function1<Continuation<? super String>, Object> block = new LaunchUnderTheHoodKt$block$1(null);

    // 3
    public static final Function1<Continuation<? super String>, Object> getBlock() {
        return block;
    }

    // 4
    static final class LaunchUnderTheHoodKt$block$1 extends SuspendLambda implements Function1<Continuation<? super String>, Object> {
        int label;

        LaunchUnderTheHoodKt$block$1(Continuation $completion) {
            super(1, $completion);
        }

        @Nullable
        public final Object invokeSuspend(@NotNull Object $result) {
            Object object = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure(SYNTHETIC_LOCAL_VARIABLE_1);
                    System.out
                            .println("Hello!");
                    this.label = 1;
                    if (DelayKt.delay(1000L, (Continuation)this) == object)
                        return object;
                    DelayKt.delay(1000L, (Continuation)this);
                    System.out
                            .println("World!");
                    return "Result";
                case 1:
                    ResultKt.throwOnFailure(SYNTHETIC_LOCAL_VARIABLE_1);
                    System.out.println("World!");
                    return "Result";
            }
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }

        @NotNull
        public final Continuation<Unit> create(@NotNull Continuation<? super LaunchUnderTheHoodKt$block$1> $completion) {
            return (Continuation<Unit>)new LaunchUnderTheHoodKt$block$1($completion);
        }

        @Nullable
        public final Object invoke(@Nullable Continuation<?> p1) {
            return ((LaunchUnderTheHoodKt$block$1)create(p1)).invokeSuspend(Unit.INSTANCE);
        }
    }

    // 5
    private static final void testStartCoroutine() {
        LaunchUnderTheHoodKt$testStartCoroutine$continuation$1 continuation = new LaunchUnderTheHoodKt$testStartCoroutine$continuation$1();
        ContinuationKt.startCoroutine(block, continuation);
    }

    // 6
    public static final class LaunchUnderTheHoodKt$testStartCoroutine$continuation$1 implements Continuation<String> {
        @NotNull
        public CoroutineContext getContext() {
            return (CoroutineContext) EmptyCoroutineContext.INSTANCE;
        }

        public void resumeWith(@NotNull Object result) {
            System.out.println(Intrinsics.stringPlus("Result is: ", Result.isFailure-impl(result) ? null : result));
        }
    }
}


internal abstract class SuspendLambda(
        public override val arity: Int,
        completion: Continuation<Any?>?
) : ContinuationImpl(completion), FunctionBase<Any?>, SuspendFunction {}

*/
