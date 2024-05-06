package online.classes.teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import online.classes.R;
import online.classes.activities.NavigationDrawer;
import online.classes.model.ClassDetails;


public class AddClassActivity extends AppCompatActivity {

    EditText address,teacherName,language,whatsappLink;
    TextView date_started,date_end;
    Spinner courseName;
    Button save_class;
    boolean isAllFieldsChecked = false;
    SharedPreferences sp;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    CollectionReference classCollection;
    ClassDetails classDetails;

    private Spinner spinnerStates;
    private Spinner spinnerDistricts;

    // Define a map to hold state-district mappings
    private Map<String, List<String>> stateDistrictMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        courseName=findViewById(R.id.courseName);
        address=findViewById(R.id.address);
        teacherName=findViewById(R.id.teacherName);
        save_class=findViewById(R.id.save_class);
        date_started=findViewById(R.id.date_started);
        date_end=findViewById(R.id.date_end);
        language=findViewById(R.id.language);
        whatsappLink=findViewById(R.id.whatsappLink);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("login", MODE_PRIVATE);
        String userId = sp.getString("UserEmail", null);

        classDetails=new ClassDetails();

        classCollection = firebaseFirestore.collection("ClassDetails");


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firebaseFirestore.collection("TeacherDetails").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                String firstname = documentSnapshot.getString("userName");
                teacherName.setText(firstname);

            } else {
                Log.d("TeacherDetails", "Document for Teacher " + userId + " does not exist");
            }
        }).addOnFailureListener(e -> {
            Log.e("TeacherDetails", "Error getting document: " + e.getMessage());
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dropdown_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseName.setAdapter(adapter);

        spinnerStates = findViewById(R.id.spinner_states);
        spinnerDistricts = findViewById(R.id.spinner_districts);

        // Create a map of Indian states to their corresponding districts
        populateStateDistrictMap();

        // Create an ArrayAdapter for the states Spinner
        ArrayAdapter<CharSequence> statesAdapter = ArrayAdapter.createFromResource(this,
                R.array.indian_states, android.R.layout.simple_spinner_item);
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStates.setAdapter(statesAdapter);

        // Set an item selected listener for the states Spinner
        spinnerStates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected state
                String selectedState = parent.getItemAtPosition(position).toString();
                // Get the list of districts for the selected state
                List<String> districts = stateDistrictMap.get(selectedState);
                if (districts != null) {
                    // Create an ArrayAdapter for the districts Spinner
                    ArrayAdapter<String> districtsAdapter = new ArrayAdapter<>(AddClassActivity.this,
                            android.R.layout.simple_spinner_item, districts);
                    districtsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDistricts.setAdapter(districtsAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no state is selected
            }
        });

        date_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date to set as the default selection in the dialog
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddClassActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Update the TextView with the selected date
                                String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth; // Format the selected date
                                date_started.setText(selectedDate);
                            }
                        }, year, month, dayOfMonth);

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });
        date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date to set as the default selection in the dialog
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddClassActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Update the TextView with the selected date
                                String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth; // Format the selected date
                                date_end.setText(selectedDate);
                            }
                        }, year, month, dayOfMonth);

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });


        save_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllFieldsChecked = CheckAllFields();
                if (isAllFieldsChecked) {
                    saveClassDetails(userId);
                }
            }
        });
    }

    private void populateStateDistrictMap() {
        stateDistrictMap = new HashMap<>();

        // Add mapping for each state
        stateDistrictMap.put("Andhra Pradesh", Arrays.asList(
                "Anantapur", "Chittoor", "East Godavari", "Guntur", "Krishna",
                "Kurnool", "Nellore", "Prakasam", "Srikakulam", "Visakhapatnam",
                "Vizianagaram", "West Godavari", "YSR Kadapa"
        ));

        // Arunachal Pradesh
        stateDistrictMap.put("Arunachal Pradesh", Arrays.asList(
                "Tawang", "West Kameng", "East Kameng", "Papum Pare", "Kurung Kumey",
                "Kra Daadi", "Lower Subansiri", "Upper Subansiri", "West Siang",
                "East Siang", "Siang", "Upper Siang", "Lower Siang", "Lower Dibang Valley",
                "Dibang Valley", "Anjaw", "Lohit", "Namsai", "Changlang", "Tirap", "Longding"
        ));

        // Assam
        stateDistrictMap.put("Assam", Arrays.asList(
                "Baksa", "Barpeta", "Biswanath", "Bongaigaon", "Cachar", "Charaideo",
                "Chirang", "Darrang", "Dhemaji", "Dhubri", "Dibrugarh", "Dima Hasao",
                "Goalpara", "Golaghat", "Hailakandi", "Hojai", "Jorhat", "Kamrup",
                "Kamrup Metropolitan", "Karbi Anglong", "Karimganj", "Kokrajhar", "Lakhimpur",
                "Majuli", "Morigaon", "Nagaon", "Nalbari", "Dima Hasao", "Sivasagar", "Sonitpur",
                "South Salmara-Mankachar", "Tinsukia", "Udalguri", "West Karbi Anglong"
        ));

        stateDistrictMap.put("Bihar", Arrays.asList(
                "Araria", "Arwal", "Aurangabad", "Banka", "Begusarai", "Bhagalpur", "Bhojpur",
                "Buxar", "Darbhanga", "East Champaran", "Gaya", "Gopalganj", "Jamui", "Jehanabad",
                "Kaimur", "Katihar", "Khagaria", "Kishanganj", "Lakhisarai", "Madhepura", "Madhubani",
                "Munger", "Muzaffarpur", "Nalanda", "Nawada", "Patna", "Purnia", "Rohtas", "Saharsa",
                "Samastipur", "Saran", "Sheikhpura", "Sheohar", "Sitamarhi", "Siwan", "Supaul",
                "Vaishali", "West Champaran"
        ));

        // Chhattisgarh
        stateDistrictMap.put("Chhattisgarh", Arrays.asList(
                "Balod", "Baloda Bazar", "Balrampur", "Bastar", "Bemetara", "Bijapur", "Bilaspur",
                "Dantewada", "Dhamtari", "Durg", "Gariaband", "Janjgir-Champa", "Jashpur", "Kabirdham",
                "Kanker", "Kondagaon", "Korba", "Koriya", "Mahasamund", "Mungeli", "Narayanpur", "Raigarh",
                "Raipur", "Rajnandgaon", "Sukma", "Surajpur", "Surguja"
        ));

        // Goa
        stateDistrictMap.put("Goa", Arrays.asList(
                "North Goa", "South Goa"
        ));

        // Gujarat
        stateDistrictMap.put("Gujarat", Arrays.asList(
                "Ahmedabad", "Amreli", "Anand", "Aravalli", "Banaskantha", "Bharuch", "Bhavnagar",
                "Botad", "Chhota Udaipur", "Dahod", "Dang", "Devbhoomi Dwarka", "Gandhinagar",
                "Gir Somnath", "Jamnagar", "Junagadh", "Kutch", "Kheda", "Mahisagar", "Mehsana",
                "Morbi", "Narmada", "Navsari", "Panchmahal", "Patan", "Porbandar", "Rajkot", "Sabarkantha",
                "Surat", "Surendranagar", "Tapi", "Vadodara", "Valsad"
        ));

// Haryana
        stateDistrictMap.put("Haryana", Arrays.asList(
                "Ambala", "Bhiwani", "Charkhi Dadri", "Faridabad", "Fatehabad", "Gurugram", "Hisar",
                "Jhajjar", "Jind", "Kaithal", "Karnal", "Kurukshetra", "Mahendragarh", "Nuh", "Palwal",
                "Panchkula", "Panipat", "Rewari", "Rohtak", "Sirsa", "Sonipat", "Yamunanagar"
        ));
// Himachal Pradesh
        stateDistrictMap.put("Himachal Pradesh", Arrays.asList(
                "Bilaspur", "Chamba", "Hamirpur", "Kangra", "Kinnaur", "Kullu", "Lahaul and Spiti",
                "Mandi", "Shimla", "Sirmaur", "Solan", "Una"
        ));

// Jharkhand
        stateDistrictMap.put("Jharkhand", Arrays.asList(
                "Bokaro", "Chatra", "Deoghar", "Dhanbad", "Dumka", "East Singhbhum", "Garhwa",
                "Giridih", "Godda", "Gumla", "Hazaribagh", "Jamtara", "Khunti", "Koderma", "Latehar",
                "Lohardaga", "Pakur", "Palamu", "Ramgarh", "Ranchi", "Sahebganj", "Seraikela Kharsawan",
                "Simdega", "West Singhbhum"
        ));
// Karnataka
        stateDistrictMap.put("Karnataka", Arrays.asList(
                "Bagalkot", "Ballari", "Belagavi", "Bengaluru Rural", "Bengaluru Urban", "Bidar",
                "Chamarajanagar", "Chikballapur", "Chikkamagaluru", "Chitradurga", "Dakshina Kannada",
                "Davanagere", "Dharwad", "Gadag", "Hassan", "Haveri", "Kalaburagi", "Kodagu", "Kolar",
                "Koppal", "Mandya", "Mysuru", "Raichur", "Ramanagara", "Shivamogga", "Tumakuru", "Udupi",
                "Uttara Kannada", "Vijayapura", "Yadgir"
        ));

// Kerala
        stateDistrictMap.put("Kerala", Arrays.asList(
                "Alappuzha", "Ernakulam", "Idukki", "Kannur", "Kasaragod", "Kollam", "Kottayam",
                "Kozhikode", "Malappuram", "Palakkad", "Pathanamthitta", "Thiruvananthapuram", "Thrissur", "Wayanad"
        ));
// Madhya Pradesh
        stateDistrictMap.put("Madhya Pradesh", Arrays.asList(
                "Agar Malwa", "Alirajpur", "Anuppur", "Ashoknagar", "Balaghat", "Barwani", "Betul",
                "Bhind", "Bhopal", "Burhanpur", "Chhatarpur", "Chhindwara", "Damoh", "Datia",
                "Dewas", "Dhar", "Dindori", "Guna", "Gwalior", "Harda", "Hoshangabad", "Indore",
                "Jabalpur", "Jhabua", "Katni", "Khandwa", "Khargone", "Mandla", "Mandsaur", "Morena",
                "Narsinghpur", "Neemuch", "Panna", "Raisen", "Rajgarh", "Ratlam", "Rewa", "Sagar",
                "Satna", "Sehore", "Seoni", "Shahdol", "Shajapur", "Sheopur", "Shivpuri", "Sidhi",
                "Singrauli", "Tikamgarh", "Ujjain", "Umaria", "Vidisha"
        ));

// Maharashtra
        stateDistrictMap.put("Maharashtra", Arrays.asList(
                "Ahmednagar", "Akola", "Amravati", "Aurangabad", "Beed", "Bhandara", "Buldhana",
                "Chandrapur", "Dhule", "Gadchiroli", "Gondia", "Hingoli", "Jalgaon", "Jalna",
                "Kolhapur", "Latur", "Mumbai City", "Mumbai Suburban", "Nagpur", "Nanded", "Nandurbar",
                "Nashik", "Osmanabad", "Palghar", "Parbhani", "Pune", "Raigad", "Ratnagiri", "Sangli",
                "Satara", "Sindhudurg", "Solapur", "Thane", "Wardha", "Washim", "Yavatmal"
        ));

// Manipur
        stateDistrictMap.put("Manipur", Arrays.asList(
                "Bishnupur", "Chandel", "Churachandpur", "Imphal East", "Imphal West", "Jiribam",
                "Kakching", "Kamjong", "Kangpokpi", "Noney", "Pherzawl", "Senapati", "Tamenglong",
                "Tengnoupal", "Thoubal", "Ukhrul"
        ));
// Meghalaya
        stateDistrictMap.put("Meghalaya", Arrays.asList(
                "East Garo Hills", "East Jaintia Hills", "East Khasi Hills", "North Garo Hills",
                "Ri Bhoi", "South Garo Hills", "South West Garo Hills", "South West Khasi Hills",
                "West Garo Hills", "West Jaintia Hills", "West Khasi Hills"
        ));

// Mizoram
        stateDistrictMap.put("Mizoram", Arrays.asList(
                "Aizawl", "Champhai", "Kolasib", "Lawngtlai", "Lunglei", "Mamit", "Saiha", "Serchhip"
        ));

// Nagaland
        stateDistrictMap.put("Nagaland", Arrays.asList(
                "Dimapur", "Kiphire", "Kohima", "Longleng", "Mokokchung", "Mon", "Peren", "Phek",
                "Tuensang", "Wokha", "Zunheboto"
        ));
// Odisha
        stateDistrictMap.put("Odisha", Arrays.asList(
                "Angul", "Balangir", "Balasore", "Bargarh", "Bhadrak", "Boudh", "Cuttack",
                "Deogarh", "Dhenkanal", "Gajapati", "Ganjam", "Jagatsinghapur", "Jajpur", "Jharsuguda",
                "Kalahandi", "Kandhamal", "Kendrapara", "Kendujhar", "Khordha", "Koraput", "Malkangiri",
                "Mayurbhanj", "Nabarangpur", "Nayagarh", "Nuapada", "Puri", "Rayagada", "Sambalpur",
                "Subarnapur", "Sundargarh"
        ));

// Punjab
        stateDistrictMap.put("Punjab", Arrays.asList(
                "Amritsar", "Barnala", "Bathinda", "Faridkot", "Fatehgarh Sahib", "Fazilka", "Ferozepur",
                "Gurdaspur", "Hoshiarpur", "Jalandhar", "Kapurthala", "Ludhiana", "Mansa", "Moga",
                "Muktsar", "Pathankot", "Patiala", "Rupnagar", "Sahibzada Ajit Singh Nagar", "Sangrur",
                "Shahid Bhagat Singh Nagar", "Sri Muktsar Sahib", "Tarn Taran"
        ));

// Rajasthan
        stateDistrictMap.put("Rajasthan", Arrays.asList(
                "Ajmer", "Alwar", "Banswara", "Baran", "Barmer", "Bharatpur", "Bhilwara", "Bikaner",
                "Bundi", "Chittorgarh", "Churu", "Dausa", "Dholpur", "Dungarpur", "Hanumangarh",
                "Jaipur", "Jaisalmer", "Jalore", "Jhalawar", "Jhunjhunu", "Jodhpur", "Karauli",
                "Kota", "Nagaur", "Pali", "Pratapgarh", "Rajsamand", "Sawai Madhopur", "Sikar",
                "Sirohi", "Sri Ganganagar", "Tonk", "Udaipur"
        ));
// Sikkim
        stateDistrictMap.put("Sikkim", Arrays.asList(
                "East Sikkim", "North Sikkim", "South Sikkim", "West Sikkim"
        ));

// Tamil Nadu
        stateDistrictMap.put("Tamil Nadu", Arrays.asList(
                "Ariyalur", "Chengalpattu", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri",
                "Dindigul", "Erode", "Kallakurichi", "Kanchipuram", "Kanyakumari", "Karur", "Krishnagiri",
                "Madurai", "Mayiladuthurai", "Nagapattinam", "Namakkal", "Nilgiris", "Perambalur",
                "Pudukkottai", "Ramanathapuram", "Ranipet", "Salem", "Sivaganga", "Tenkasi", "Thanjavur",
                "Theni", "Thoothukudi", "Tiruchirappalli", "Tirunelveli", "Tirupathur", "Tiruppur", "Tiruvallur",
                "Tiruvannamalai", "Tiruvarur", "Vellore", "Viluppuram", "Virudhunagar"
        ));

// Telangana
        stateDistrictMap.put("Telangana", Arrays.asList(
                "Adilabad", "Bhadradri Kothagudem", "Hyderabad", "Jagtial", "Jangaon", "Jayashankar Bhupalapally",
                "Jogulamba Gadwal", "Kamareddy", "Karimnagar", "Khammam", "Komaram Bheem Asifabad",
                "Mahabubabad", "Mahabubnagar", "Mancherial", "Medak", "Medchal–Malkajgiri", "Mulugu", "Nagarkurnool",
                "Nalgonda", "Narayanpet", "Nirmal", "Nizamabad", "Peddapalli", "Rajanna Sircilla", "Rangareddy",
                "Sangareddy", "Siddipet", "Suryapet", "Vikarabad", "Wanaparthy", "Warangal Rural", "Warangal Urban",
                "Yadadri Bhuvanagiri"
        ));
// Tripura
        stateDistrictMap.put("Tripura", Arrays.asList(
                "Dhalai", "Gomati", "Khowai", "North Tripura", "Sepahijala", "South Tripura", "Unakoti", "West Tripura"
        ));

// Uttar Pradesh
        stateDistrictMap.put("Uttar Pradesh", Arrays.asList(
                "Agra", "Aligarh", "Ambedkar Nagar", "Amethi", "Amroha", "Auraiya", "Ayodhya", "Azamgarh",
                "Baghpat", "Bahraich", "Ballia", "Balrampur", "Banda", "Barabanki", "Bareilly", "Basti",
                "Bhadohi", "Bijnor", "Budaun", "Bulandshahr", "Chandauli", "Chitrakoot", "Deoria", "Etah",
                "Etawah", "Farrukhabad", "Fatehpur", "Firozabad", "Gautam Buddh Nagar", "Ghaziabad", "Ghazipur",
                "Gonda", "Gorakhpur", "Hamirpur", "Hapur", "Hardoi", "Hathras", "Jalaun", "Jaunpur", "Jhansi",
                "Kannauj", "Kanpur Dehat", "Kanpur Nagar", "Kasganj", "Kaushambi", "Kushinagar", "Lakhimpur Kheri",
                "Lalitpur", "Lucknow", "Maharajganj", "Mahoba", "Mainpuri", "Mathura", "Mau", "Meerut", "Mirzapur",
                "Moradabad", "Muzaffarnagar", "Pilibhit", "Pratapgarh", "Prayagraj", "Raebareli", "Rampur",
                "Saharanpur", "Sambhal", "Sant Kabir Nagar", "Shahjahanpur", "Shamli", "Shrawasti", "Siddharthnagar",
                "Sitapur", "Sonbhadra", "Sultanpur", "Unnao", "Varanasi"
        ));

// Uttarakhand
        stateDistrictMap.put("Uttarakhand", Arrays.asList(
                "Almora", "Bageshwar", "Chamoli", "Champawat", "Dehradun", "Haridwar", "Nainital", "Pauri Garhwal",
                "Pithoragarh", "Rudraprayag", "Tehri Garhwal", "Udham Singh Nagar", "Uttarkashi"
        ));
// West Bengal
        stateDistrictMap.put("West Bengal", Arrays.asList(
                "Alipurduar", "Bankura", "Birbhum", "Cooch Behar", "Dakshin Dinajpur", "Darjeeling",
                "Hooghly", "Howrah", "Jalpaiguri", "Jhargram", "Kalimpong", "Kolkata", "Malda", "Murshidabad",
                "Nadia", "North 24 Parganas", "Paschim Bardhaman", "Paschim Medinipur", "Purba Bardhaman",
                "Purba Medinipur", "Purulia", "South 24 Parganas", "Uttar Dinajpur"
        ));

// Andaman and Nicobar Islands
        stateDistrictMap.put("Andaman and Nicobar Islands", Arrays.asList(
                "Nicobar", "North and Middle Andaman", "South Andaman"
        ));
// Chandigarh
        stateDistrictMap.put("Chandigarh", Arrays.asList(
                "Chandigarh"
        ));

// Dadra and Nagar Haveli and Daman and Diu
        stateDistrictMap.put("Dadra and Nagar Haveli and Daman and Diu", Arrays.asList(
                "Dadra and Nagar Haveli", "Daman", "Diu"
        ));
// Delhi
        stateDistrictMap.put("Delhi", Arrays.asList(
                "Central Delhi", "East Delhi", "New Delhi", "North Delhi", "North East Delhi",
                "North West Delhi", "Shahdara", "South Delhi", "South East Delhi", "South West Delhi",
                "West Delhi"
        ));
// Lakshadweep
        stateDistrictMap.put("Lakshadweep", Arrays.asList(
                "Agatti", "Amini", "Andrott", "Bitra", "Chetlat", "Kadmat", "Kalpeni", "Kavaratti", "Kiltan", "Minicoy"
        ));
// Puducherry
        stateDistrictMap.put("Puducherry", Arrays.asList(
                "Karaikal", "Mahe", "Puducherry", "Yanam"
        ));


    }

    public void saveClassDetails(String userId){

        classDetails.setSubject(courseName.getSelectedItem().toString());
        classDetails.setState(spinnerStates.getSelectedItem().toString());
        classDetails.setDistrict(spinnerDistricts.getSelectedItem().toString());
        classDetails.setAddress(address.getText().toString());
        classDetails.setTeacherName(teacherName.getText().toString());
        classDetails.setStartedDate(date_started.getText().toString());
        classDetails.setEndDate(date_end.getText().toString());
        classDetails.setLanguage(language.getText().toString());
        classDetails.setWhatsappLink(whatsappLink.getText().toString());

        CollectionReference subjectCollection = firebaseFirestore.collection("ClassDetails").document(userId).collection("Subject");

        subjectCollection.document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Class already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            subjectCollection.document(courseName.getSelectedItem().toString()).set(classDetails)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Class Added Successfully", Toast.LENGTH_SHORT).show();
                                            goToMainActivity();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Fail to add data " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }
    private boolean CheckAllFields() {



        if (date_started.getText().toString().isEmpty()) {
            date_started.setError("Starting Date is required");
            return false;
        }
        if (date_end.getText().toString().isEmpty()) {
            date_end.setError("End Date is required");
            return false;
        }

        if (address.getText().toString().isEmpty()) {
            address.setError("Address is required");
            return false;
        }

        if (teacherName.getText().toString().isEmpty()) {
            teacherName.setError("Teacher Name is required");
            return false;
        }
        if(language.getText().toString().isEmpty()){
            language.setError("Language is required");
        }

        return true;
    }

    public void goToMainActivity() {
        Intent i = new Intent(AddClassActivity.this, NavigationDrawer.class);
        startActivity(i);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default:return super.onOptionsItemSelected(item);
        }
    }
}