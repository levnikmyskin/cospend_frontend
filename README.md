# Cospend Android Frontend

## Contributing 
A `docker-compose.yml` is provided at the root of the repository. You can run a development instance of nextcloud by running:
```bash
docker compose up
```

### Nextcloud 
Nextcloud will be available at `http://localhost:8080`. **NOTICE** that in the android emulator this is equivalent to `http://10.0.2.2:8080`.  
In order to actually test login, communications, etc. with the Nextcloud instance, **you need to**:  

  1. navigate to the Nextcloud data;
    * You can retrieve the path by executing `docker volume inspect cospend-frontend_nextcloud-data`;
  2. edit (probably with root access) the `config/config.php` file, and add:
  ```php
    // ...
    'trusted_domains' => 
        array (
            0 => 'localhost:8080',  // this you should already have
            1 => '10.0.2.2:8080'  // this you should add
        ),
    // ...
   ```
  3. you should good to go
