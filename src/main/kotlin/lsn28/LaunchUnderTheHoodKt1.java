package lsn28;

import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
public final class LaunchUnderTheHoodKt {
    public static final void main() {
        testLaunch();
        Thread.sleep(2000L);
    }

    private static final void testLaunch() {
        CoroutineScope scope = CoroutineScopeKt.CoroutineScope((CoroutineContext) JobKt.Job$default(null, 1, null));
        BuildersKt.launch$default(scope, null, null, new LaunchUnderTheHoodKt$testLaunch$1(null), 3, null);
    }

    static final class LaunchUnderTheHoodKt$testLaunch$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        int label;

        LaunchUnderTheHoodKt$testLaunch$1(Continuation $completion) {
            super(2, $completion);
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
                    return Unit.INSTANCE;
                case 1:
                    ResultKt.throwOnFailure(SYNTHETIC_LOCAL_VARIABLE_1);
                    System.out.println("World!");
                    return Unit.INSTANCE;
            }
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }

        @NotNull
        public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<? super LaunchUnderTheHoodKt$testLaunch$1> $completion) {
            return (Continuation<Unit>)new LaunchUnderTheHoodKt$testLaunch$1($completion);
        }

        @Nullable
        public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<?> p2) {
            return ((LaunchUnderTheHoodKt$testLaunch$1)create(p1, p2)).invokeSuspend(Unit.INSTANCE);
        }
    }
}
*/
