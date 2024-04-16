import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smd.surmaiya.Fragments.HomeFragment
import com.smd.surmaiya.Fragments.SearchFragment
import com.smd.surmaiya.R

class BottomNavigationHelper(private val activity: AppCompatActivity) {

    fun setUpBottomNavigation() {
        val bottomNavigationView = activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    Log.d("BottomNavigationHelper", "Home Fragment loaded")
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_search -> {
                    loadFragment(SearchFragment())
                    Log.d("BottomNavigationHelper", "Search Fragment loaded")
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

     fun loadFragment(fragment: Fragment) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

        Log.d("BottomNavigationHelper", "Fragment loaded: ${fragment.javaClass.simpleName}")
    }
}
