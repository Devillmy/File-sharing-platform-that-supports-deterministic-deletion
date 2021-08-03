#include "SGXGenerateKey_u.h"
#include <errno.h>

typedef struct ms_GeneratePassword_t {
	int ms_pwd_size;
	int* ms_random;
	char* ms_Password;
	uint8_t* ms_SealedPwd;
	uint32_t ms_data_size;
} ms_GeneratePassword_t;

typedef struct ms_get_sealed_data_size_t {
	uint32_t ms_retval;
} ms_get_sealed_data_size_t;

typedef struct ms_seal_data_t {
	sgx_status_t ms_retval;
	uint8_t* ms_sealed_blob;
	uint32_t ms_data_size;
} ms_seal_data_t;

typedef struct ms_unseal_data_t {
	sgx_status_t ms_retval;
	const uint8_t* ms_sealed_blob;
	size_t ms_data_size;
} ms_unseal_data_t;

typedef struct ms_sgx_oc_cpuidex_t {
	int* ms_cpuinfo;
	int ms_leaf;
	int ms_subleaf;
} ms_sgx_oc_cpuidex_t;

typedef struct ms_sgx_thread_wait_untrusted_event_ocall_t {
	int ms_retval;
	const void* ms_self;
} ms_sgx_thread_wait_untrusted_event_ocall_t;

typedef struct ms_sgx_thread_set_untrusted_event_ocall_t {
	int ms_retval;
	const void* ms_waiter;
} ms_sgx_thread_set_untrusted_event_ocall_t;

typedef struct ms_sgx_thread_setwait_untrusted_events_ocall_t {
	int ms_retval;
	const void* ms_waiter;
	const void* ms_self;
} ms_sgx_thread_setwait_untrusted_events_ocall_t;

typedef struct ms_sgx_thread_set_multiple_untrusted_events_ocall_t {
	int ms_retval;
	const void** ms_waiters;
	size_t ms_total;
} ms_sgx_thread_set_multiple_untrusted_events_ocall_t;

static sgx_status_t SGX_CDECL SGXGenerateKey_sgx_oc_cpuidex(void* pms)
{
	ms_sgx_oc_cpuidex_t* ms = SGX_CAST(ms_sgx_oc_cpuidex_t*, pms);
	sgx_oc_cpuidex(ms->ms_cpuinfo, ms->ms_leaf, ms->ms_subleaf);

	return SGX_SUCCESS;
}

static sgx_status_t SGX_CDECL SGXGenerateKey_sgx_thread_wait_untrusted_event_ocall(void* pms)
{
	ms_sgx_thread_wait_untrusted_event_ocall_t* ms = SGX_CAST(ms_sgx_thread_wait_untrusted_event_ocall_t*, pms);
	ms->ms_retval = sgx_thread_wait_untrusted_event_ocall(ms->ms_self);

	return SGX_SUCCESS;
}

static sgx_status_t SGX_CDECL SGXGenerateKey_sgx_thread_set_untrusted_event_ocall(void* pms)
{
	ms_sgx_thread_set_untrusted_event_ocall_t* ms = SGX_CAST(ms_sgx_thread_set_untrusted_event_ocall_t*, pms);
	ms->ms_retval = sgx_thread_set_untrusted_event_ocall(ms->ms_waiter);

	return SGX_SUCCESS;
}

static sgx_status_t SGX_CDECL SGXGenerateKey_sgx_thread_setwait_untrusted_events_ocall(void* pms)
{
	ms_sgx_thread_setwait_untrusted_events_ocall_t* ms = SGX_CAST(ms_sgx_thread_setwait_untrusted_events_ocall_t*, pms);
	ms->ms_retval = sgx_thread_setwait_untrusted_events_ocall(ms->ms_waiter, ms->ms_self);

	return SGX_SUCCESS;
}

static sgx_status_t SGX_CDECL SGXGenerateKey_sgx_thread_set_multiple_untrusted_events_ocall(void* pms)
{
	ms_sgx_thread_set_multiple_untrusted_events_ocall_t* ms = SGX_CAST(ms_sgx_thread_set_multiple_untrusted_events_ocall_t*, pms);
	ms->ms_retval = sgx_thread_set_multiple_untrusted_events_ocall(ms->ms_waiters, ms->ms_total);

	return SGX_SUCCESS;
}

static const struct {
	size_t nr_ocall;
	void * func_addr[5];
} ocall_table_SGXGenerateKey = {
	5,
	{
		(void*)(uintptr_t)SGXGenerateKey_sgx_oc_cpuidex,
		(void*)(uintptr_t)SGXGenerateKey_sgx_thread_wait_untrusted_event_ocall,
		(void*)(uintptr_t)SGXGenerateKey_sgx_thread_set_untrusted_event_ocall,
		(void*)(uintptr_t)SGXGenerateKey_sgx_thread_setwait_untrusted_events_ocall,
		(void*)(uintptr_t)SGXGenerateKey_sgx_thread_set_multiple_untrusted_events_ocall,
	}
};

sgx_status_t GeneratePassword(sgx_enclave_id_t eid, int pwd_size, int* random, char* Password, uint8_t* SealedPwd, uint32_t data_size)
{
	sgx_status_t status;
	ms_GeneratePassword_t ms;
	ms.ms_pwd_size = pwd_size;
	ms.ms_random = random;
	ms.ms_Password = Password;
	ms.ms_SealedPwd = SealedPwd;
	ms.ms_data_size = data_size;
	status = sgx_ecall(eid, 0, &ocall_table_SGXGenerateKey, &ms);
	return status;
}

sgx_status_t get_sealed_data_size(sgx_enclave_id_t eid, uint32_t* retval)
{
	sgx_status_t status;
	ms_get_sealed_data_size_t ms;
	status = sgx_ecall(eid, 1, &ocall_table_SGXGenerateKey, &ms);
	if (status == SGX_SUCCESS && retval) *retval = ms.ms_retval;
	return status;
}

sgx_status_t seal_data(sgx_enclave_id_t eid, sgx_status_t* retval, uint8_t* sealed_blob, uint32_t data_size)
{
	sgx_status_t status;
	ms_seal_data_t ms;
	ms.ms_sealed_blob = sealed_blob;
	ms.ms_data_size = data_size;
	status = sgx_ecall(eid, 2, &ocall_table_SGXGenerateKey, &ms);
	if (status == SGX_SUCCESS && retval) *retval = ms.ms_retval;
	return status;
}

sgx_status_t unseal_data(sgx_enclave_id_t eid, sgx_status_t* retval, const uint8_t* sealed_blob, size_t data_size)
{
	sgx_status_t status;
	ms_unseal_data_t ms;
	ms.ms_sealed_blob = sealed_blob;
	ms.ms_data_size = data_size;
	status = sgx_ecall(eid, 3, &ocall_table_SGXGenerateKey, &ms);
	if (status == SGX_SUCCESS && retval) *retval = ms.ms_retval;
	return status;
}

