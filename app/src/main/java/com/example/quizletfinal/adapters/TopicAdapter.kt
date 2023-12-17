
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletfinal.R
import com.example.quizletfinal.models.OnItemClickListener
import com.example.quizletfinal.models.Topic
import java.util.Locale

class TopicAdapter(
    private val context: Context,
    var topicList: MutableList<Topic>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TopicAdapter.ViewHolder>(), Filterable {

    private val topicListFull = topicList.toList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txtTopicName)

        fun bind(topic: Topic) {
            titleTextView.text = topic.title
            itemView.setOnClickListener {
                onItemClickListener.onItemClickListener(topic)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.topic_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = topicList[position]
        holder.bind(topic)
    }

    override fun getItemCount(): Int {
        return topicList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList: List<Topic> = if (constraint == null || constraint.isEmpty()) {
                    topicListFull
                } else {
                    val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                    topicListFull.filter {
                        it.title?.lowercase(Locale.getDefault())?.contains(filterPattern) == true
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                topicList = (results?.values as MutableList<Topic>?)!!
                notifyDataSetChanged()
            }
        }
    }
}