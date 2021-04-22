package ee.smkv.calc.loan;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ee.smkv.calc.loan.calculators.AnnuityCalculator;
import ee.smkv.calc.loan.calculators.Calculator;
import ee.smkv.calc.loan.calculators.DifferentiatedCalculator;
import ee.smkv.calc.loan.calculators.FixedPaymentCalculator;
import ee.smkv.calc.loan.export.Exporter;
import ee.smkv.calc.loan.export.FileCreationException;
import ee.smkv.calc.loan.model.Loan;
import ee.smkv.calc.loan.utils.*;

import java.io.File;
import java.math.BigDecimal;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
  public static final String SETTINGS_NAME = MainActivity.class.getName();
  public static StoreManager storeManager;

  public static final Calculator[] CALCULATORS = new Calculator[]{new AnnuityCalculator(), new DifferentiatedCalculator(), new FixedPaymentCalculator()};

  private static final String ZERO = "0";

  private static final int LOAN_INIT = 0;
  private static final int LOAN_INVALID = 1;
  private static final int LOAN_VALID = 2;
  private static final int LOAN_CALCULATED = 3;
  private static final String IS_ADVANCED_SHOWED = "IsAdvancedShowed";
  protected static final PriceInputFilter PRICE_INPUT_FILTER = new PriceInputFilter();

  private int loanState = LOAN_INIT;

  TextView fixedPaymentLabel, periodLabel, resultPeriodTotalText, resultMonthlyPaymentText, resultAmountTotalText, resultInterestTotalText, moreText, resultDownPaymentTotalText, resultCommissionsTotalText, effectiveInterestText;

  EditText amountEdit, interestEdit, fixedPaymentEdit, periodYearEdit, periodMonthEdit, downPaymentEdit, disposableCommissionEdit, monthlyCommissionEdit, effectiveRateEdit , residueEdit;

  Spinner loanTypeSpinner;

  Button calculateButton, scheduleButton, typeHelpButton, typeHelpCloseButton, periodYearPlusButton, periodYearMinusButton, periodMonthPlusButton, periodMonthMinusButton, effectiveRateBtn;

  PercentValueSwitchButton downPaymentButton, disposableCommissionButton, monthlyCommissionButton , residueButton;

  ScrollView mainScrollView;

  ViewGroup resultContainer, periodLayout, advancedViewGroup, resultDownPaymentGroupView, resultCommissionsGroupView;

  Loan loan = new Loan();

  Calculator calculator;
  private AlertDialog effectiveRateToNominalDialog;


  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    init();
    setPriceInputFilter(amountEdit, interestEdit, fixedPaymentEdit, periodYearEdit, periodMonthEdit, downPaymentEdit, disposableCommissionEdit, monthlyCommissionEdit, effectiveRateEdit , residueEdit);
    setIconsToButtons();
    loadSharedPreferences();
    registerEventListeners();
    if (isLoanReadyForCalculation(loan)) {
      calculate();
    }
  }

  private void loadSharedPreferences() {
    try {
      storeManager = new StoreManager(getSharedPreferences(SETTINGS_NAME, 0));
      storeManager.loadTextViews(amountEdit, interestEdit, fixedPaymentEdit, periodYearEdit, periodMonthEdit, downPaymentEdit, disposableCommissionEdit, monthlyCommissionEdit , residueEdit);
      storeManager.loadSpinners(loanTypeSpinner);
      storeManager.loadPercentButtons(downPaymentButton, disposableCommissionButton, monthlyCommissionButton, residueButton);
      if (periodYearEdit.getText() == null || periodYearEdit.getText().length() == 0) {
        periodYearEdit.setText(ZERO);
      }
      if (periodMonthEdit.getText() == null || periodMonthEdit.getText().length() == 0) {
        periodMonthEdit.setText(ZERO);
      }

        setupAdvancedButton(true);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

    private void setupAdvancedButton( boolean init) {
        boolean show = init ? storeManager.getBoolean(IS_ADVANCED_SHOWED) : advancedViewGroup.getVisibility() != View.VISIBLE;
        advancedViewGroup.setVisibility( show ? View.VISIBLE : View.GONE);
        moreText.setText( show ? R.string.hide : R.string.expand);
        int arrow = show ? R.drawable.arrowup : R.drawable.arrowdown ;
        Drawable img = getApplicationContext().getResources().getDrawable(arrow);
        moreText.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
    }


    @Override
  protected void onStop() {
    super.onStop();
    try {
      storeManager.storeTextViews(amountEdit, interestEdit, fixedPaymentEdit, periodYearEdit, periodMonthEdit, downPaymentEdit, disposableCommissionEdit, monthlyCommissionEdit , residueEdit);
      storeManager.storeSpinners(loanTypeSpinner);
      storeManager.storePercentButtons(downPaymentButton, disposableCommissionButton, monthlyCommissionButton , residueButton);
      storeManager.setBoolean(IS_ADVANCED_SHOWED, advancedViewGroup.getVisibility() == View.VISIBLE);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void init() {
    effectiveRateEdit = new EditText(this);
    effectiveRateEdit.setInputType(InputType.TYPE_CLASS_PHONE);

    amountEdit = (EditText)findViewById(R.id.amountEdit);
    interestEdit = (EditText)findViewById(R.id.interestEdit);
    fixedPaymentEdit = (EditText)findViewById(R.id.fixedPaymentEdit);
    periodYearEdit = (EditText)findViewById(R.id.periodYearEdit);
    periodMonthEdit = (EditText)findViewById(R.id.periodMonthEdit);
    downPaymentEdit = (EditText)findViewById(R.id.downPaymentEdit);
    disposableCommissionEdit = (EditText)findViewById(R.id.disposableCommissionEdit);
    monthlyCommissionEdit = (EditText)findViewById(R.id.monthlyCommissionEdit);
    residueEdit = (EditText)findViewById(R.id.residueEdit);

    resultMonthlyPaymentText = (TextView)findViewById(R.id.resultMonthlyPaymentText);
    resultAmountTotalText = (TextView)findViewById(R.id.resultAmountTotalText);
    resultInterestTotalText = (TextView)findViewById(R.id.resultIterestTotalText);
    resultPeriodTotalText = (TextView)findViewById(R.id.resultPeriodTotalText);
    fixedPaymentLabel = (TextView)findViewById(R.id.fixedPaymentLabel);
    periodLabel = (TextView)findViewById(R.id.periodLabel);
    moreText = (TextView)findViewById(R.id.moreText);
    resultDownPaymentTotalText = (TextView)findViewById(R.id.resultDownPaymentTotalText);
    resultCommissionsTotalText = (TextView)findViewById(R.id.resultCommissionsTotalText);
    effectiveInterestText = (TextView)findViewById(R.id.effectiveInterestText);

    loanTypeSpinner = (Spinner)findViewById(R.id.loanTypeSpinner);

    calculateButton = (Button)findViewById(R.id.calculateButton);
    periodYearPlusButton = (Button)findViewById(R.id.periodYearPlusButton);
    periodYearMinusButton = (Button)findViewById(R.id.periodYearMinusButton);
    periodMonthPlusButton = (Button)findViewById(R.id.periodMonthPlusButton);
    periodMonthMinusButton = (Button)findViewById(R.id.periodMonthMinusButton);
    scheduleButton = (Button)findViewById(R.id.scheduleButton);
    typeHelpButton = (Button)findViewById(R.id.loanTypeHelpButton);
    typeHelpCloseButton = (Button)findViewById(R.id.typeHelpCloseButton);
    effectiveRateBtn = (Button)findViewById(R.id.effectiveRateBtn);

    mainScrollView = (ScrollView)findViewById(R.id.mainScrollView);
    resultContainer = (ViewGroup)findViewById(R.id.resultContainer);
    periodLayout = (ViewGroup)findViewById(R.id.periodLayout);
    advancedViewGroup = (ViewGroup)findViewById(R.id.advancedViewGroup);
    resultDownPaymentGroupView = (ViewGroup)findViewById(R.id.resultDownPaymentGroupView);
    resultCommissionsGroupView = (ViewGroup)findViewById(R.id.resultCommissionsGroupView);

    downPaymentButton = (PercentValueSwitchButton)findViewById(R.id.downPaymentButton);
    disposableCommissionButton = (PercentValueSwitchButton)findViewById(R.id.disposableCommissionButton);
    monthlyCommissionButton = (PercentValueSwitchButton)findViewById(R.id.monthlyCommissionButton);
    residueButton = (PercentValueSwitchButton)findViewById(R.id.residueButton);

    AlertDialog.Builder effectiveRateToNominalDialogBuilder = new AlertDialog.Builder(this);
    effectiveRateToNominalDialogBuilder.setTitle(this.getString(R.string.eff2NominalConvTitle));
    effectiveRateToNominalDialogBuilder.setMessage(this.getString(R.string.eff2NominalConvMessage));
    effectiveRateToNominalDialogBuilder.setView(effectiveRateEdit);
    effectiveRateToNominalDialogBuilder.setPositiveButton(this.getString(R.string.calc), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        try {
          // http://www.miniwebtool.com/nominal-interest-rate-calculator/
          // i = n × ((1 + r)1/n - 1)
          // nominalRate = 12 × ((1 + effectiveRate)1/12 - 1)
          BigDecimal effectiveRate = ViewUtil.getBigDecimalValue(effectiveRateEdit);
          BigDecimal nominalRate = new BigDecimal(1200 * (Math.pow(1 + effectiveRate.doubleValue() / 100, (double)1 / (double)12) - 1));
          interestEdit.setText(ViewUtil.formatBigDecimal(nominalRate));

        }
        catch (Exception e) {
          showError(e);
        }

      }
    });

    effectiveRateToNominalDialogBuilder.setNegativeButton(this.getString(R.string.close), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        // Canceled.
      }
    });
    effectiveRateToNominalDialog = effectiveRateToNominalDialogBuilder.create();
  }


  private void setIconsToButtons() {
    calculateButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.calculator), null, null, null);
    scheduleButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.table), null, null, null);
    typeHelpButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.help), null, null, null);
  }

  private void registerEventListeners() {
    loanTypeSpinner.setOnItemSelectedListener(this);
    calculateButton.setOnClickListener(this);
    scheduleButton.setOnClickListener(this);
    typeHelpButton.setOnClickListener(this);
    periodYearPlusButton.setOnClickListener(this);
    periodYearMinusButton.setOnClickListener(this);
    periodMonthPlusButton.setOnClickListener(this);
    periodMonthMinusButton.setOnClickListener(this);
    effectiveRateBtn.setOnClickListener(this);
    moreText.setOnClickListener(this);

    MyTextWatcher myYearTextWatcher = new MyTextWatcher() {
      public void onChange(Editable editable) {
        checkFixPeriod(periodYearEdit, editable);
        invalidateLoan();
      }
    };

    MyTextWatcher myMonthTextWatcher = new MyTextWatcher() {
      public void onChange(Editable editable) {
        checkFixPeriod(periodMonthEdit, editable);
        invalidateLoan();
      }
    };
    periodYearEdit.addTextChangedListener(myYearTextWatcher);
    periodMonthEdit.addTextChangedListener(myMonthTextWatcher);

    MyTextWatcher invalidateWatcher = new MyTextWatcher() {
      public void onChange(Editable editable) {
        invalidateLoan();
      }
    };


    downPaymentEdit.addTextChangedListener(invalidateWatcher);
    disposableCommissionEdit.addTextChangedListener(invalidateWatcher);
    monthlyCommissionEdit.addTextChangedListener(invalidateWatcher);
    residueEdit.addTextChangedListener(invalidateWatcher);

    downPaymentButton.setOnClickListener(this);
    disposableCommissionButton.setOnClickListener(this);
    monthlyCommissionButton.setOnClickListener(this);
    residueButton.setOnClickListener(this);



  }

  private void setPriceInputFilter(EditText... fields) {
    for (EditText field : fields) {
      field.setFilters(new InputFilter[]{PRICE_INPUT_FILTER});
    }

  }

  private void invalidateLoan() {
    loanState = LOAN_INVALID;
    scheduleButton.setEnabled(false);
  }

  public void onClick(View view) {
    try {
      if (view == calculateButton) {
        calculate();
        if (loanState == LOAN_CALCULATED) {
          mainScrollView.scrollTo(resultContainer.getLeft(), resultContainer.getTop());
        }
      }
      else if (view == scheduleButton) {
        showSchedule();
      }
      else if (view == typeHelpButton) {
        startActivity(new Intent(MainActivity.this, TypeHelpActivity.class));
      }
      else if (view == periodYearPlusButton) {
        periodYearEdit.setText(Integer.valueOf(ViewUtil.getIntegerValue(periodYearEdit) + 1).toString());
      }
      else if (view == periodYearMinusButton) {
        periodYearEdit.setText(Integer.valueOf(ViewUtil.getIntegerValue(periodYearEdit) - 1).toString());
      }
      else if (view == periodMonthPlusButton) {
        periodMonthEdit.setText(Integer.valueOf(ViewUtil.getIntegerValue(periodMonthEdit) + 1).toString());
      }
      else if (view == periodMonthMinusButton) {
        periodMonthEdit.setText(Integer.valueOf(ViewUtil.getIntegerValue(periodMonthEdit) - 1).toString());
      }
      else if (view == effectiveRateBtn) {

        effectiveRateButtonPressed();

      }
      else if (view == moreText) {
        setupAdvancedButton(false);
      }
      else if (view == downPaymentButton || view == disposableCommissionButton || view == monthlyCommissionButton|| view == residueButton) {
        invalidateLoan();
      }
    }
    catch (EditTextNumberFormatException e) {
      if (e.editText == periodYearEdit) {
        periodYearEdit.setText(ZERO);
      }
      else if (e.editText == periodMonthEdit) {
        periodMonthEdit.setText(ZERO);
      }
    }

  }

  private void effectiveRateButtonPressed() {
    effectiveRateToNominalDialog.show();
  }

  private void checkFixPeriod(EditText editText, Editable editable) {
    Integer max = editText == periodMonthEdit ? 12 : 50;
    try {
      Integer value = ViewUtil.getIntegerValue(editText);
      if (value > max) {
        editable.clear();
        editable.append(max.toString());
      }
      else if (value < 0) {
        editable.clear();
        editable.append(ZERO);
      }
    }
    catch (EditTextNumberFormatException e) {
      editable.clear();
      editable.append(ZERO);
    }
  }

  private void changeCalculatorType() {
    setTitle((String)loanTypeSpinner.getSelectedItem());
    calculator = CALCULATORS[loanTypeSpinner.getSelectedItemPosition()];
    boolean isFixedPayment = calculator instanceof FixedPaymentCalculator;
    fixedPaymentLabel.setVisibility(isFixedPayment ? View.VISIBLE : View.GONE);
    fixedPaymentEdit.setVisibility(isFixedPayment ? View.VISIBLE : View.GONE);
    periodLabel.setVisibility(isFixedPayment ? View.GONE : View.VISIBLE);
    periodLayout.setVisibility(isFixedPayment ? View.GONE : View.VISIBLE);
  }


  private void showSchedule() {
    ScheduleTabActivity.loan = loan;
    startActivity(new Intent(MainActivity.this, ScheduleTabActivity.class));
  }


  protected void showError(Exception e) {
    new ErrorDialogWrapper(this, e).show();
  }

  protected void showError(int id) {
    new ErrorDialogWrapper(this, getResources().getString(id)).show();
  }

  private void calculate() {
    try {
      invalidateLoan();
      loan = new Loan();
      loadLoanDataFromUI();
      if (isLoanReadyForCalculation(loan)) {
        loanState = LOAN_VALID;
        calculator.calculate(loan);
        showCalculatedData();
        scheduleButton.setEnabled(true);
        loanState = LOAN_CALCULATED;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      showError(e);
    }
  }

  private boolean loadLoanDataFromUI() {
    boolean isFixedPayment = calculator instanceof FixedPaymentCalculator;
    try {
      loan.setLoanType(loanTypeSpinner.getSelectedItemPosition());
      loan.setAmount(ViewUtil.getBigDecimalValue(amountEdit));
      loan.setInterest(ViewUtil.getBigDecimalValue(interestEdit));

      if (isFixedPayment) {
        loan.setFixedPayment(ViewUtil.getBigDecimalValue(fixedPaymentEdit));
      }
      else {
        int months = ViewUtil.getIntegerValue(periodMonthEdit);
        int years = ViewUtil.getIntegerValue(periodYearEdit);
        loan.setPeriod(years * 12 + months);
      }

      if (loan.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        showError(R.string.errorAmount);
        return false;
      }
      else if (loan.getInterest().compareTo(BigDecimal.ZERO) <= 0) {
        showError(R.string.errorInterest);
        return false;
      }
      else if (!isFixedPayment && loan.getPeriod() <= 0) {
        showError(R.string.errorPeriod);
        return false;
      }
      else if (isFixedPayment && loan.getFixedPayment().compareTo(BigDecimal.ZERO) <= 0) {
        showError(R.string.errorFixedAmount);
        return false;
      }

      loan.setDownPaymentType(downPaymentButton.isPercent() ? Loan.PERCENT : Loan.VALUE);
      loan.setDisposableCommissionType(disposableCommissionButton.isPercent() ? Loan.PERCENT : Loan.VALUE);
      loan.setMonthlyCommissionType(monthlyCommissionButton.isPercent() ? Loan.PERCENT : Loan.VALUE);
      loan.setResidueType(residueButton.isPercent() ? Loan.PERCENT : Loan.VALUE);

      loan.setDownPayment(ViewUtil.getBigDecimalValue(downPaymentEdit));
      loan.setDisposableCommission(ViewUtil.getBigDecimalValue(disposableCommissionEdit));
      loan.setMonthlyCommission(ViewUtil.getBigDecimalValue(monthlyCommissionEdit));
      loan.setResidue(ViewUtil.getBigDecimalValue(residueEdit));


    }
    catch (EditTextNumberFormatException e) {
      if (e.editText == amountEdit) {
        showError(R.string.errorAmount);
      }
      else if (e.editText == interestEdit) {
        showError(R.string.errorInterest);
      }
      else if (e.editText == fixedPaymentEdit) {
        showError(R.string.errorFixedAmount);
      }
      else if (e.editText == downPaymentEdit) {
        showError(R.string.errorDownPayment);
      }
      else if (e.editText == disposableCommissionEdit) {
        showError(R.string.errorDispCommission);
      }
      else if (e.editText == monthlyCommissionEdit) {
        showError(R.string.errorMonthlyCommission);
      }else if (e.editText == residueEdit ) {
        showError(R.string.errorResidue);
      }
      e.editText.requestFocus();
      mainScrollView.scrollTo(e.editText.getLeft(), e.editText.getTop());
      return false;
    }
    return true;
  }

  private void showCalculatedData() {
    String monthlyPayment = "";
    BigDecimal max = loan.getMaxMonthlyPayment();
    BigDecimal min = loan.getMinMonthlyPayment();
    if (max.compareTo(min) == 0) {
      monthlyPayment = ViewUtil.formatBigDecimal(max);
    }
    else {
      monthlyPayment = ViewUtil.formatBigDecimal(max) + " - " + ViewUtil.formatBigDecimal(min);
    }
    resultMonthlyPaymentText.setText(monthlyPayment);

    BigDecimal totalAmount = loan.getTotalAmount();

    if (loan.getDownPaymentPayment() != null && loan.getDownPaymentPayment().compareTo(BigDecimal.ZERO) != 0) {
      resultDownPaymentGroupView.setVisibility(View.VISIBLE);
      resultDownPaymentTotalText.setText(ViewUtil.formatBigDecimal(loan.getDownPaymentPayment()));
    }
    else {
      resultDownPaymentGroupView.setVisibility(View.GONE);
    }

    if (loan.getCommissionsTotal() != null && loan.getCommissionsTotal().compareTo(BigDecimal.ZERO) != 0) {
      resultCommissionsGroupView.setVisibility(View.VISIBLE);
      resultCommissionsTotalText.setText(ViewUtil.formatBigDecimal(loan.getCommissionsTotal()));
    }
    else {
      resultCommissionsGroupView.setVisibility(View.GONE);
    }


    resultAmountTotalText.setText(ViewUtil.formatBigDecimal(totalAmount));
    resultInterestTotalText.setText(ViewUtil.formatBigDecimal(loan.getTotalInterests()));
    resultPeriodTotalText.setText(loan.getPeriod().toString());

    effectiveInterestText.setText(ViewUtil.formatBigDecimal(loan.getEffectiveInterestRate()));

    Toast.makeText(this, getResources().getText(R.string.msgCalculated), Toast.LENGTH_SHORT).show();
  }

  private boolean isLoanReadyForCalculation(Loan loan) {
    if (loan.getAmount() == null || loan.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      return false;
    }

    if (loan.getInterest() == null || loan.getInterest().compareTo(BigDecimal.ZERO) <= 0) {
      return false;
    }
    boolean isFixedPayment = calculator instanceof FixedPaymentCalculator;
    if (isFixedPayment && (loan.getFixedPayment() == null || loan.getFixedPayment().compareTo(BigDecimal.ZERO) <= 0)) {
      return false;
    }

    if (!isFixedPayment && (loan.getPeriod() == null || loan.getPeriod() <= 0)) {
      return false;
    }
    return true;
  }


  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    changeCalculatorType();
    invalidateLoan();
    if (isLoanReadyForCalculation(loan)) {
      loanState = LOAN_VALID;
      calculate();
    }
  }

  public void onNothingSelected(AdapterView<?> adapterView) {
    //ignore
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.mainmenu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {

    if (loanState < LOAN_CALCULATED && item.getItemId() != R.id.viewCompareMenu && item.getItemId() != R.id.donateMenu) {
      DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int i) {
          if (i == DialogInterface.BUTTON_POSITIVE) {
            calculate();
          }
          menuAction(item);

        }
      };

      new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.recalculateLoanQuestion)).setCancelable(false).setPositiveButton(getResources().getString(R.string.yes), onClickListener).setNegativeButton(getResources().getString(R.string.no), onClickListener).create().show();

    }
    else {
      menuAction(item);
    }

    return true;
  }

  private boolean menuAction(MenuItem item) {
    try {
      switch (item.getItemId()) {
        case R.id.addToCompareMenu:
          if (loan != null) {
            storeManager.addLoan(loan);
            openCompareActivity();
          }
          break;
        case R.id.viewCompareMenu:
          openCompareActivity();
          break;
        case R.id.exportEmailMenu:
          Exporter.sendToEmail(loan, getResources(), this);
          break;
        case R.id.exportExcelMenu:
          File file = Exporter.exportToCSVFile(loan, getResources(), this);
          new OkDialogWrapper(this, getResources().getString(R.string.fileCreated) + ' ' + file.getAbsolutePath()).show();
          break;

        case R.id.donateMenu:
          Uri uri = Uri.parse("http://samkov.pri.ee/simple-loan-calculator/");
          startActivity(new Intent(Intent.ACTION_VIEW, uri));
          break;

        case R.id.clean:
            cleanForm();
            break;
      }
    }
    catch (FileCreationException e) {
      new ErrorDialogWrapper(this, e.getMessage()).show();
    }
    return true;
  }

    private void cleanForm() {
      cleanEdit("",amountEdit, interestEdit, fixedPaymentEdit,  downPaymentEdit, disposableCommissionEdit, monthlyCommissionEdit, effectiveRateEdit , residueEdit);
      cleanEdit("0",periodYearEdit, periodMonthEdit);
    }

    private void cleanEdit(String value , EditText ... editTexts){
        for (EditText editText : editTexts){
            editText.getText().clear();
            editText.getText().append(value);
        }
    }


    private void openCompareActivity() {
    startActivity(new Intent(MainActivity.this, CompareActivity.class));
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {

    if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
      setInputType(InputType.TYPE_NULL, amountEdit, interestEdit, fixedPaymentEdit, periodYearEdit, periodMonthEdit, downPaymentEdit, disposableCommissionEdit, monthlyCommissionEdit, effectiveRateEdit , residueEdit);
      //Toast.makeText(this, "HARD-keyboard", Toast.LENGTH_SHORT).show();
    }
    else {
      setInputType(InputType.TYPE_CLASS_PHONE, amountEdit, interestEdit, fixedPaymentEdit, periodYearEdit, periodMonthEdit, downPaymentEdit, disposableCommissionEdit, monthlyCommissionEdit, effectiveRateEdit , residueEdit);
      //Toast.makeText(this, "SOFT-keyboard", Toast.LENGTH_SHORT).show();
    }

    super.onConfigurationChanged(newConfig);
  }

  private void setInputType(int type, EditText... fields) {
    for (EditText field : fields) {
      field.setInputType(type);
    }
  }
}