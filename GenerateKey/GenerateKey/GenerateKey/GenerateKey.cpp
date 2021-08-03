#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>
#include <stdio.h>
#include <tchar.h>
#include "sgx_urts.h"
#include <string.h>
#include "SGXGenerateKey_u.h"

#define ENCLAVE_FILE _T("SGXGenerateKey.signed.dll")

int main()
{
	sgx_enclave_id_t	eid;
	sgx_status_t		ret = SGX_SUCCESS;
	sgx_launch_token_t	token = { 0 };
	int updated = 0;
	int random[16];
	int rando;
	char Password[16]="Hello!";
	
	srand((unsigned)time(NULL));
	for (int i = 0; i < 16; i++) {
		random[i] = rand() % (26);
		//printf("%d\n",random[i]);
	}
	rando = 16;
	
	ret = sgx_create_enclave(ENCLAVE_FILE, SGX_DEBUG_FLAG, &token, &updated, &eid, NULL);
	
	if (ret != SGX_SUCCESS)
	{
		printf("APP:error %#x ,failed to create enclave .\n", ret);
		return -1;
	}
	uint32_t sealed_data_size = 0;
	ret = get_sealed_data_size(eid, &sealed_data_size);
	if (ret != SGX_SUCCESS)
	{
		printf("failed!\n");
		return false;
	}
	else if (sealed_data_size == UINT32_MAX)
	{
		printf("failed1!\n");
		return false;
	}

	uint8_t *SealedPwd = (uint8_t *)malloc(sealed_data_size);
	//printf("sealed_data_size=%d\n", sealed_data_size);
	if (SealedPwd == NULL)
	{
		printf("Failed!\n");
		return false;
	}
	
	//printf("Password = %s\n", Password);
	//printf("Password = %s\n", SealedPwd);
	GeneratePassword(eid, rando, random, Password, SealedPwd, sealed_data_size);
	printf("Password = %s\n", Password);
	//printf("Password = %s\n", SealedPwd);
	//free(Password);
	//sgx_destroy_enclave(eid);
	if (SGX_SUCCESS != sgx_destroy_enclave(eid))
		return -1;
	getchar();
	return 0;
}
