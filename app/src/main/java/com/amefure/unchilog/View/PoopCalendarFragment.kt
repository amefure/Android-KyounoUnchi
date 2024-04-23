package com.amefure.unchilog.View

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.unchilog.R
import com.amefure.unchilog.Repository.SCCalender.SCCalenderRepository
import com.amefure.unchilog.View.RecycleViewSetting.PoopCalendarAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class PoopCalendarFragment : Fragment() {

    var sc = SCCalenderRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_poop_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch {
            sc.currentDates.collect {
                val recyclerView: RecyclerView = view.findViewById(R.id.main_list)
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 7, RecyclerView.VERTICAL, false)
                recyclerView.addItemDecoration(
                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                )
                recyclerView.adapter = PoopCalendarAdapter(it)
            }
        }
    }

}