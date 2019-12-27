import io.kotlintest.koin.KoinListener

abstract class DBTestWithKoin : DBTest() {
    override fun listeners() = super.listeners() + listOf(KoinListener(listOf(daoModule, apiModule)))
}
