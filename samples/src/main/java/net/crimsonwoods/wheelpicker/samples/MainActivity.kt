package net.crimsonwoods.wheelpicker.samples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale
import net.crimsonwoods.wheelpicker.WheelPicker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<WheelPicker>(R.id.picker).apply {
            adapter = Adapter()
        }
    }

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView by lazy { itemView.findViewById(R.id.text) }
    }

    private class Adapter : RecyclerView.Adapter<ViewHolder>() {
        private val locales =
            Locale.getAvailableLocales()
                .toList()
                .filter {
                    it.getDisplayCountry(it).isNotEmpty() && it.getDisplayLanguage(it).isNotEmpty()
                }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent.inflate())
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val locale = locales[position]
            holder.textView.text = holder.itemView.context.getString(
                R.string.item_format,
                locale.getDisplayCountry(locale),
                locale.getDisplayLanguage(locale)
            )
        }

        override fun getItemCount(): Int = locales.size

        private fun ViewGroup.inflate(): View {
            return LayoutInflater.from(context).inflate(R.layout.picker_item, this, false)
        }
    }
}