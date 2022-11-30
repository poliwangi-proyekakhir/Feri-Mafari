@inject('helper', 'App\Helpers\HelpersFunction')
@extends('admin.layout')
@section('content')
<!-- Content Header (Page header) -->
<section class="content-header">
   <div class="container-fluid">
      <div class="row mb-2">
         <div class="col-sm-6">
            <h1>Data Permohonan Mutasi Dana</h1>
         </div>
         <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
               <li class="breadcrumb-item"><a href="{{ URL::to('/admin') }}">Home</a></li>
               <li class="breadcrumb-item active">Data Permohonan Mutasi Dana</li>
            </ol>
         </div>
      </div>
   </div>
   <!-- /.container-fluid -->
</section>
<section class="content">
   <div class="container-fluid">
      <div class="row">
         <div class="col-12">
            <!-- <div class="alert alert-warning" role="alert">
                
            </div> -->
            <div class="card">
               <div class="card-header container-fluid">
                  <div class="row">
                     <div class="col-md-10">
                        <h3 class="card-title">Data Permohonan Mutasi Dana</h3>
                     </div>
                     <div class="col-md-2">
                        <!-- <a href="{{ URL::to('/pemilik/permohonan-penarikan-dana') }}" class="btn btn-primary float-right">Ajukan Penarikan</a>               -->
                     </div>
                  </div>
               </div>
               <!-- /.card-header -->
               <div class="card-body">
                  <table id="dataTables" class="table table-bordered table-striped">
                     <thead>
                        <tr>
                           <th>Tanggal</th>
                           <th>Saldo</th>
                           <th>Nominal</th>
                           <th>Rek. Tujuan</th>
                           <th>Keterangan</th>  
                           <th>#</th>                           
                           
                        </tr>
                     </thead>
                     <tbody>
                        @foreach($data as $d)
                        <tr>
                           <td>{{ $d->created_at }}</td>
                           <td>{{ $helper->rupiah($d->saldo) }}</td>
                           <td>{!! $helper->rupiah($d->nominal) !!}</td>
                           <td>{!! $d->rekening_bank !!}</td>
                           <td>{!! $d->keterangan !!}</td>                          
                           <td>
                              <a href="{!! URL::to('/admin/permohonan-mutasi-diproses/' . sha1($d->id)) !!}" class="btn btn-success btn-xs">Proses</a>
                           </td>
                        </tr>
                        @endforeach
                     </tbody>
                  </table>
               </div>
               <!-- /.card-body -->
            </div>
            <!-- /.card -->
         </div>
      </div>
   </div>
</section>
<script>
   $(function () {
     
     $('#dataTables').DataTable({
       "paging": true,
       "lengthChange": false,
       "searching": true,
       "ordering": true,
       "info": true,
       "autoWidth": false,
       "responsive": true,
     });
   });
</script>
@endsection