import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smd.surmaiya.Fragments.AddAlbumFragment
import com.smd.surmaiya.Fragments.AddSongFragment
import com.smd.surmaiya.Fragments.HomeFragment
import com.smd.surmaiya.Fragments.LibraryFragment
import com.smd.surmaiya.Fragments.SearchFragment
import com.smd.surmaiya.ManagerClasses.UserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.UserType

class BottomNavigationHelper(private val activity: AppCompatActivity) {

    fun setUpBottomNavigation() {

        val bottomNavigationView = activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)


        val currentUser = UserManager.getCurrentUser()
        val menu = bottomNavigationView.menu
        val artistItem = menu.findItem(R.id.navigation_upload)
        artistItem.isVisible = currentUser?.userType == UserType.ARTIST

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
                R.id.navigation_library -> {
                    if(UserManager.getCurrentUser()== null){
                        UserManager.showGuestDialog(activity)
                        return@setOnNavigationItemSelectedListener false
                    }
                    loadFragment(LibraryFragment())
                    Log.d("BottomNavigationHelper", "Library Fragment loaded")
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_upload -> {
                    Log.d("BottomNavigationHelper", "Upload Fragment loaded")
                    loadFragment(AddAlbumFragment())
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
