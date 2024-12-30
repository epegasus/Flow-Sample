## Kotlin Flows

### Flows Topic
1) Flows
2) Callback Flows

```
interface Api {
    fun fetchData(callback: Callback<String>)
}
  
interface Callback<T> {
    fun onSuccess(data: T)
    fun onError(error: Throwable)
}
```
