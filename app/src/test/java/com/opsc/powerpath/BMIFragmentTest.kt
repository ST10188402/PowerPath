import com.opsc.powerpath.BMIFragment
import org.junit.Assert.assertEquals
import org.junit.Test

class BMIFragmentTest {

    @Test
    fun testCalculateBMI() {
        val bmiFragment = BMIFragment()

        // Test cases
        val testCases = listOf(
            Triple(170f, 70f, 24.22f), // Normal case
            Triple(160f, 50f, 19.53f), // Underweight case
            Triple(180f, 90f, 27.78f), // Overweight case
            Triple(150f, 100f, 44.44f), // Obese case
            Triple(0f, 70f, 0f) // Edge case: height is zero
        )

        for ((height, weight, expectedBMI) in testCases) {
            val calculatedBMI = bmiFragment.calculateBMI(height, weight)
            assertEquals(expectedBMI, calculatedBMI, 0.01f)
        }
    }
}