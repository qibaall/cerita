package com.example.cerita.presentation.main

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.cerita.DummyData
import com.example.cerita.MainDispatcherRule
import com.example.cerita.StoryPagingSourcesTest
import com.example.cerita.data.UserRepository
import com.example.cerita.data.response.ListStoryItem
import com.example.cerita.getOrAwaitValue

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatchersRules = MainDispatcherRule()

    @Mock
    private lateinit var repository: UserRepository

    private lateinit var mainViewModel: MainViewModel

    private lateinit var mockedLog: MockedStatic<Log>

    @Before
    fun setUp() {
        mockedLog = Mockito.mockStatic(Log::class.java)
        mockedLog.`when`<Boolean> {
            Log.isLoggable(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        }.thenReturn(true)

        val expectedList = MutableLiveData<PagingData<ListStoryItem>>()
        Mockito.`when`(repository.getStories()).thenReturn(expectedList)
        mainViewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStoriesResponse = DummyData.generateDummyStories()
        val expectedList = MutableLiveData<PagingData<ListStoryItem>>()
        val dataDummy: PagingData<ListStoryItem> =
            StoryPagingSourcesTest.snapShot(dummyStoriesResponse)

        Mockito.`when`(repository.getStories()).thenReturn(expectedList)

        expectedList.value = dataDummy


        val mainVm = MainViewModel(repository)
        val actualList: PagingData<ListStoryItem> = mainVm.story.getOrAwaitValue()

        val diff = AsyncPagingDataDiffer(
            diffCallback = ListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        diff.submitData(actualList)
        Assert.assertNotNull(diff.snapshot())
        Assert.assertEquals(dummyStoriesResponse.size, diff.snapshot().size)
        Assert.assertEquals(dummyStoriesResponse[0], diff.snapshot()[0])
    }


    @Test
    fun `when Get List Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedList = MutableLiveData<PagingData<ListStoryItem>>()
        expectedList.value = data
        Mockito.`when`(repository.getStories()).thenReturn(expectedList)

        val mainVm = MainViewModel(repository)
        val actualList: PagingData<ListStoryItem> = mainVm.story.getOrAwaitValue()
        val diff = AsyncPagingDataDiffer(
            diffCallback = ListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        diff.submitData(actualList)
        Assert.assertEquals(0, diff.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}