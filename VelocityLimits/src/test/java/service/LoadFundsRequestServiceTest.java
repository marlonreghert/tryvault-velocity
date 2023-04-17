package service;

import com.tryvault.constants.LoadFundsRequestLimits;
import com.tryvault.entity.LoadFundsRequestEntity;
import com.tryvault.model.LoadFundsRequest;
import com.tryvault.model.LoadFundsResponse;
import com.tryvault.repository.LoadFundsRequestRepository;
import com.tryvault.service.LoadFundsRequestService;
import com.tryvault.util.LoadFundsResponseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoadFundsRequestServiceTest {
    @InjectMocks
    private LoadFundsRequestService loadFundsRequestService;

    @Mock
    private LoadFundsRequestRepository loadFundsRequestRepository;

    @Mock
    private LoadFundsResponseBuilder loadFundsResponseBuilder;

    @Captor
    private ArgumentCaptor<LoadFundsRequestEntity> loadRequestEntityCaptor;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private static final BigDecimal LOAD_AMOUNT = new BigDecimal("100");
    private static final ZonedDateTime REQUEST_TIME = ZonedDateTime.now(ZoneOffset.UTC);
    private static final BigDecimal AMOUNT_PER_WEEK = new BigDecimal("500");
    private static final BigDecimal TOTAL_AMOUNT_LOADED_THIS_WEEK = new BigDecimal("400");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessLoadAttempt_Success() {
        // Mock input load funds request
        LoadFundsRequest loadFundsRequest = new LoadFundsRequest();
        loadFundsRequest.setId(1L);
        loadFundsRequest.setCustomerId(123456L);
        loadFundsRequest.setLoadAmount(new BigDecimal("100.00"));
        loadFundsRequest.setTime(ZonedDateTime.now());

        // Mock repository behavior
        when(loadFundsRequestRepository.existsByIdAndCustomerId(anyLong(), anyLong())).thenReturn(false);
        when(loadFundsRequestRepository.countByCustomerIdAndTimeBetweenAndAccepted(anyLong(), any(), any(), eq(true))).thenReturn(2L);
        when(loadFundsRequestRepository.sumLoadAmountByCustomerIdAndTimeBetween(anyLong(), any(), any(), eq(true))).thenReturn(new BigDecimal("150.00"));
        when(loadFundsRequestRepository.sumLoadAmountByCustomerIdAndTimeBetween(anyLong(), any(), any(), eq(true))).thenReturn(new BigDecimal("200.00"));
        when(loadFundsRequestRepository.save(any(LoadFundsRequestEntity.class))).thenReturn(new LoadFundsRequestEntity());

        // Call the method to be tested
        LoadFundsResponse result = loadFundsRequestService.processLoadAttempt(loadFundsRequest);

        // Assertions
        assertTrue(result.isAccepted());
        assertEquals("1", result.getId());
    }

    @Test
    public void testProcessLoadAttempt_ExceedMaxAmountPerDay() {
        // Create a load funds request with amount that exceeds the maximum amount per day
        long id = 1L;
        long customerId = 12345L;
        BigDecimal loadAmount = LoadFundsRequestLimits.AMOUNT_PER_DAY.add(BigDecimal.ONE); // Exceed maximum amount per day
        ZonedDateTime requestTime = ZonedDateTime.now();

        LoadFundsRequest loadFundsRequest = new LoadFundsRequest(id, customerId, loadAmount, requestTime);

        // Mock the repository to return a total amount loaded today that exceeds the maximum amount per day
        BigDecimal totalAmountLoadedToday = LoadFundsRequestLimits.AMOUNT_PER_DAY.add(BigDecimal.ONE); // Exceed maximum amount per day
        when(loadFundsRequestRepository.sumLoadAmountByCustomerIdAndTimeBetween(eq(customerId), any(ZonedDateTime.class), any(ZonedDateTime.class), eq(true)))
                .thenReturn(totalAmountLoadedToday);

        // Invoke the method to be tested
        LoadFundsResponse loadFundsResponse = loadFundsRequestService.processLoadAttempt(loadFundsRequest);

        // Verify that the repository methods were called
        verify(loadFundsRequestRepository, times(1)).sumLoadAmountByCustomerIdAndTimeBetween(eq(customerId), any(ZonedDateTime.class), any(ZonedDateTime.class), eq(true));
        verify(loadFundsRequestRepository, atMostOnce()).save(any(LoadFundsRequestEntity.class));

        // Verify that the response object is not null and has the expected properties
        assertNotNull(loadFundsResponse);
        assertFalse(loadFundsResponse.isAccepted());
    }


    @Test
    public void testProcessLoadAttempt_ExceedMaxLoadsPerDay() {
        // Mock input load funds request
        LoadFundsRequest loadFundsRequest = new LoadFundsRequest();
        loadFundsRequest.setId(1L);
        loadFundsRequest.setCustomerId(123456L);
        loadFundsRequest.setLoadAmount(new BigDecimal("100.00"));
        loadFundsRequest.setTime(ZonedDateTime.now());

        // Mock repository behavior
        when(loadFundsRequestRepository.existsByIdAndCustomerId(anyLong(), anyLong())).thenReturn(false);
        when(loadFundsRequestRepository.countByCustomerIdAndTimeBetweenAndAccepted(anyLong(), any(), any(), eq(true))).thenReturn(3L);

        // Call the method to be tested
        LoadFundsResponse result = loadFundsRequestService.processLoadAttempt(loadFundsRequest);

        // Assertions
        assertFalse(result.isAccepted());
    }

    @Test
    public void testProcessLoadAttempt_AmountPerWeekExceeded() {
        // Mock input
        long id = 1L;
        long customerId = 12345L;
        BigDecimal loadAmount = new BigDecimal("100.00");
        ZonedDateTime requestTime = ZonedDateTime.now();
        LoadFundsRequest loadFundsRequest = new LoadFundsRequest(id, customerId, loadAmount, requestTime);

        // Mock repository
        when(loadFundsRequestRepository.existsByIdAndCustomerId(anyLong(), anyLong())).thenReturn(false);
        when(loadFundsRequestRepository.countByCustomerIdAndTimeBetweenAndAccepted(anyLong(), any(), any(), eq(true))).thenReturn(2L); // 2 loads today
        when(loadFundsRequestRepository.sumLoadAmountByCustomerIdAndTimeBetween(anyLong(), any(), any(), eq(true))).thenReturn(new BigDecimal("30000.00")); // $400 loaded this week

        // Mock response builder
        LoadFundsResponseBuilder loadFundsResponseBuilder = mock(LoadFundsResponseBuilder.class);
        when(loadFundsResponseBuilder.accepted(anyBoolean())).thenReturn(loadFundsResponseBuilder);
        when(loadFundsResponseBuilder.build()).thenReturn(new LoadFundsResponse(String.valueOf(id), String.valueOf(customerId), false));

        // Call the method being tested
        LoadFundsResponse result = loadFundsRequestService.processLoadAttempt(loadFundsRequest);

        // Verify repository methods are called with correct arguments
        verify(loadFundsRequestRepository, times(1)).existsByIdAndCustomerId(eq(id), eq(customerId));
        verify(loadFundsRequestRepository, times(1)).countByCustomerIdAndTimeBetweenAndAccepted(eq(customerId), any(), any(), eq(true));
        verify(loadFundsRequestRepository, times(1)).sumLoadAmountByCustomerIdAndTimeBetween(eq(customerId), any(), any(), eq(true));

        // Verify the result
        assert !result.isAccepted();
        assert result.getId().equals(String.valueOf(id));
        assert result.getCustomerId().equals(String.valueOf(customerId));
    }

    @Test
    public void testProcessLoadAttempt_AmountPerWeekAccepts() {
        // Mock input
        long id = 1L;
        long customerId = 12345L;
        BigDecimal loadAmount = new BigDecimal("100.00");
        ZonedDateTime requestTime = ZonedDateTime.parse("2017-05-25T19:07:34.190912345+05:30[Asia/Calcutta]");

        // Check if the customer has exceeded the maximum number of loads per day
        ZonedDateTime startOfDay = requestTime.toLocalDate().atStartOfDay(ZoneOffset.UTC);
        LocalDate startOfWeek = requestTime.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));



        LoadFundsRequest loadFundsRequest = new LoadFundsRequest(id, customerId, loadAmount, requestTime);

        // Mock repository
        when(loadFundsRequestRepository.existsByIdAndCustomerId(anyLong(), anyLong())).thenReturn(false);
        when(loadFundsRequestRepository.countByCustomerIdAndTimeBetweenAndAccepted(anyLong(), any(), any(), eq(true))).thenReturn(2L); // 2 loads today
        when(loadFundsRequestRepository.sumLoadAmountByCustomerIdAndTimeBetween(anyLong(), eq(startOfDay), any(), eq(true))).thenReturn(new BigDecimal("100.00"));
        when(loadFundsRequestRepository.sumLoadAmountByCustomerIdAndTimeBetween(anyLong(), eq(startOfWeek.atStartOfDay(ZoneOffset.UTC)), any(), eq(true))).thenReturn(new BigDecimal("10000.00"));

        // Call the method being tested
        LoadFundsResponse result = loadFundsRequestService.processLoadAttempt(loadFundsRequest);

        // Verify repository methods are called with correct arguments
        verify(loadFundsRequestRepository, times(1)).existsByIdAndCustomerId(eq(id), eq(customerId));
        verify(loadFundsRequestRepository, times(1)).countByCustomerIdAndTimeBetweenAndAccepted(eq(customerId), any(), any(), eq(true));
        verify(loadFundsRequestRepository, times(2)).sumLoadAmountByCustomerIdAndTimeBetween(eq(customerId), any(), any(), eq(true));

        // Verify the result
        assert result.isAccepted();
        assert result.getId().equals(String.valueOf(id));
        assert result.getCustomerId().equals(String.valueOf(customerId));
    }
}
