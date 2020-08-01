package com.example.attendancetaking;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

public class AttendanceAdapter extends BaseExpandableListAdapter {
        private List<Attendance> attendList;
        private Context context;
        private Student s;
        private Module m;
        private DBHelper dbhelper;
        private String modName;
        private String totalNum;
        private String studentID;
        private String nameStudent;


        public AttendanceAdapter(Context context, List<Attendance> attendance) {
            this.context=context;
            attendList=attendance;
            dbhelper= new DBHelper(this.context);
        }

        //----------Get GROUP methods-------------
        @Override
        public Object getGroup(int groupPosition){
            return this.attendList.get(groupPosition);
        }
        @Override
        public int getGroupCount(){
            return attendList.size();
        }
        @Override
        public long getGroupId(int groupPosition){
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View v= convertView;
            // Check if an existing view is being reused, otherwise inflate the view
            if (v == null) {
                LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_row_attendancegrp, null);
            }

            //DBHelper dbhelper= new DBHelper(this.context);
            Attendance a= (Attendance)getGroup(groupPosition);
            // Lookup view for data population
            TextView modCode = (TextView) v.findViewById(R.id.tvAttendanceCode);
            TextView attendanceType = (TextView) v.findViewById(R.id.tvAttendanceType);
            TextView modFullName = (TextView) v.findViewById(R.id.tvAttendanceModuleName);
            TextView date = (TextView) v.findViewById(R.id.tvAttendanceDate);
            TextView time = (TextView) v.findViewById(R.id.tvAttendanceTime);
            TextView sessionPasswd = (TextView) v.findViewById(R.id.tvAttendanceSessionPassword);
            TextView totalPaxPresent = (TextView) v.findViewById(R.id.tvAttendancePresentStudentsNum);
            // Populate the data into the template view using the data object
            attendanceType.setText(a.getAttendanceType());
            modCode.setText(a.getAttendanceCode());
            date.setText(a.getDated());
            time.setText(a.getTimed());
            sessionPasswd.setText(a.getPasscode());
            Cursor c= dbhelper.getModuleName(a.getAttendanceCode());
            if (c.moveToFirst()) {
                modName = c.getString(c.getColumnIndex(DBHelper.COLUMN_MOD_NAME));
            }
            c.close();
            modFullName.setText(modName);
            Cursor d= dbhelper.getModuleMaxCapacity(a.getAttendanceCode());
            if (d.moveToFirst()) {
                totalNum = d.getString(d.getColumnIndex(DBHelper.COLUMN_MOD_TOTALPAX));
            }
            d.close();
            if(a.getPresentStudents().get(0).length()>0){
                totalPaxPresent.setText("Present: "+a.getPresentStudents().size()+"/"+totalNum);
            }
            else {
                totalPaxPresent.setText("Present: 0/" + totalNum);
            }

            // Return the completed view to render on screen
            return v;
        }

         //----------Get CHILD methods-------------
        @Override
        public Object getChild(int groupPosition,int childPosition){
            return attendList.get(groupPosition).getPresentStudents().get(childPosition);
        }
        @Override
        public long getChildId(int groupPosition, int childPosition){
            return childPosition;
        }
        @Override
        public int getChildrenCount(int groupPosition){

            return attendList.get(groupPosition).getPresentStudents().size();

        }
        @Override
        public View getChildView(int groupPosition, final int childPosition,boolean isLastChild,View convertView,ViewGroup parent){
            nameStudent= (String)getChild(groupPosition,childPosition);
            View v= convertView;

            if(v==null){
                LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v= inflater.inflate(R.layout.list_row_attendancechild,null);
            }
            TextView tvPresentName= v.findViewById(R.id.tvAttendanceChildStudName);
            TextView tvPresentID= v.findViewById(R.id.tvAttendanceChildStudID);
            Cursor e= dbhelper.getStudentIDByName(nameStudent);
            if (e.moveToFirst()) {
                studentID = e.getString(e.getColumnIndex(DBHelper.COLUMN_STUDID));
            }
            e.close();
            tvPresentName.setText(nameStudent);
            if(nameStudent.isEmpty()){
                tvPresentID.setText("");
            }
            else {
                tvPresentID.setText(studentID);
            }


            return v;
        }

        @Override
        public boolean hasStableIds(){
            return false;
        }
        @Override
        public boolean isChildSelectable(int groupPosition,int childPosition){
            return true;
        }
}
