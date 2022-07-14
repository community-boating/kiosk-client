find . -name "*.xml" -exec sed -i '' 's/android.support.constraint.ConstraintLayout/androidx.constraintlayout.widget.ConstraintLayout/g' {} \;
find . -name "*.xml" -exec sed -i '' 's/android.support.constraintlayout.Guideline/androidx.constraintlayout.widget.Guideline/g' {} \;
find . -name "*.xml" -exec sed -i '' 's/android.support.v7.widget.AppCompatTextView/androidx.appcompat.widget.AppCompatTextView/g' {} \;
find . -name "*.xml" -exec sed -i '' 's/android.support.v7.widget.AppCompatButton/androidx.appcompat.widget.AppCompatButton/g' {} \;
