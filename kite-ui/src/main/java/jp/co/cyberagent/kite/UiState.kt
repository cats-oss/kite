package jp.co.cyberagent.kite

sealed class UiState<out T> {
  object Loading : UiState<Nothing>()

  data class Success<T>(val value: T) : UiState<T>()

  data class Failure(val e: Throwable) : UiState<Nothing>()

  fun isLoading(): Boolean = this is Loading

  fun valueOrNull(): T? {
    return (this as? Success<T>)?.value
  }

  fun exceptionOrNull(): Throwable? {
    return (this as? Failure)?.e
  }

  fun <R> map(f: (T) -> R): UiState<R> {
    return if (this is Success) {
      f(value).success()
    } else {
      @Suppress("UNCHECKED_CAST")
      this as UiState<R>
    }
  }
}

fun <T> T.success() = UiState.Success(this)

fun Throwable.failure() = UiState.Failure(this)

fun loading() = UiState.Loading

inline fun <T> KiteProperty<UiState<T>>.update(
  action: (T?) -> T
) {
  value = loading()
  value = try {
    action.invoke(valueOrNull()).success()
  } catch (e: Exception) {
    e.failure()
  }
}

fun <T> KiteProperty<UiState<T>>.setValue(v: T) {
  value = v.success()
}

fun <T> KiteGetter<UiState<T>>.valueOrNull(): T? = value.valueOrNull()

fun <T> KiteGetter<UiState<T>>.exceptionOrNull(): Throwable? = value.exceptionOrNull()
