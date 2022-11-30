<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Kos;
use App\Models\MutasiDana;
use Illuminate\Support\Facades\URL;

class WebViewController extends Controller
{
    
    public function getLocation($id){

        $kos = Kos::where('id','=',$id)->first();
        
        return view('webview.maps',['lat' => $kos->lat,'lng'=> $kos->lng]);
    }


    public function getQrcode($id){

        return view('webview.qrcode',['kos_id' => $id]);
    }

    public function mutasiDana($user_id){
        $mutasi       = MutasiDana::where('user_id', '=', $user_id)->orderBy('updated_at', 'desc')->get();
        $mutasi_masuk = MutasiDana::where('user_id', '=', $user_id)
            ->where('status', '=', 'IN')
            ->get()->sum('nominal');

        $mutasi_keluar = MutasiDana::where('user_id', '=', $user_id)
            ->where('status', '=', 'OUT')
            ->get()->sum('nominal');

        return view('webview.mutasidana.list', ['data' => $mutasi, 'dana_tersisa' => $mutasi_masuk - $mutasi_keluar,'user_id' => $user_id]);

    }


    public function permohonanPenarikanDana($user_id, Request $request)
    {
        $mutasi_masuk = MutasiDana::where('user_id', '=', $user_id)
            ->where('status', '=', 'IN')
            ->get()->sum('nominal');

        $mutasi_keluar = MutasiDana::where('user_id', '=', $user_id)
            ->where('status', '=', 'OUT')
            ->get()->sum('nominal');

        $dana_tersisa = $mutasi_masuk - $mutasi_keluar;

        switch ($request->method()) {
            case 'POST':

                $this->validate($request, [

                    'bank_nama'     => 'required',
                    'bank_rekening' => 'required',
                    'bank_penerima' => 'required',
                    'nominal'       => 'required|numeric|min:10000|max:' . $dana_tersisa,

                ]);

                MutasiDana::create([
                    'user_id'       => $user_id,
                    'rekening_bank' => $request->bank_nama . '|' . $request->bank_rekening . '|' . $request->bank_penerima,
                    'nominal'       => $request->nominal,
                    'status'        => 'PND',
                    'keterangan'    => 'Permohonan penarikan dana ' . date('Y-m-d H:i:s'),
                ]);

                return redirect(URL::to('/webview/mutasi-dana/' . $user_id));

                break;
            case 'GET':

                return view('webview.mutasidana.tambah',['user_id' => $user_id]);

                break;

            default:
                # code...
                break;
        }
    }
}
