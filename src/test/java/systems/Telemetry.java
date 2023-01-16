package systems;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import services.ECU;

public class Telemetry implements ITestListener {
    public void onTestStart(ITestResult iTestResult) {

    }

    public void onTestSuccess(ITestResult iTestResult) {
        ECU.extentTest.log(Status.PASS,MarkupHelper.createLabel(iTestResult.getName().toUpperCase()+" PASS",ExtentColor.GREEN));

    }

    public void onTestFailure(ITestResult iTestResult) {
        ECU.extentTest.log(Status.FAIL,iTestResult.getThrowable().getMessage());
        ECU.extentTest.log(Status.FAIL, MarkupHelper.createLabel(iTestResult.getName().toUpperCase()+" FAIL", ExtentColor.RED));
    }

    public void onTestSkipped(ITestResult iTestResult) {
        ECU.extentTest.log(Status.SKIP,MarkupHelper.createLabel(iTestResult.getName().toUpperCase()+" SKIPPED",ExtentColor.PURPLE));

    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    public void onStart(ITestContext iTestContext) {

    }

    public void onFinish(ITestContext iTestContext) {

    }
}
