find . -name "*.xml" -exec sed -i '' 's/androidx.constraintlayout.ConstraintLayout/androidx.constraintlayout.widget.ConstraintLayout/g' {} \;
find . -name "*.xml" -exec sed -i '' 's/androidx.constraintlayout.Guideline/androidx.constraintlayout.widget.Guideline/g' {} \;

